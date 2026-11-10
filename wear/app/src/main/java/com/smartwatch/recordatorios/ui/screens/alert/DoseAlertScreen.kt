package com.smartwatch.recordatorios.ui.screens.alert

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnScope
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.smartwatch.recordatorios.R
import com.smartwatch.recordatorios.alarm.SnoozePolicy
import com.smartwatch.recordatorios.data.local.DoseEntity
import com.smartwatch.recordatorios.data.local.DoseStatus
import com.smartwatch.recordatorios.ui.components.BwActionPill
import com.smartwatch.recordatorios.ui.components.BwBanner
import com.smartwatch.recordatorios.ui.components.BwButton
import com.smartwatch.recordatorios.ui.components.BwEdgeButton
import com.smartwatch.recordatorios.ui.components.BwIcon
import com.smartwatch.recordatorios.ui.components.BwScreenScaffold
import com.smartwatch.recordatorios.ui.components.BwSnoozeMark
import com.smartwatch.recordatorios.ui.components.BwSuccessMark
import com.smartwatch.recordatorios.ui.components.BwText
import com.smartwatch.recordatorios.ui.components.MedicationLabel
import com.smartwatch.recordatorios.ui.components.Tomi
import com.smartwatch.recordatorios.ui.components.TomiMood
import com.smartwatch.recordatorios.ui.components.doseStatusText
import com.smartwatch.recordatorios.ui.components.edgeItem
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.BwTone
import com.smartwatch.recordatorios.ui.theme.RecordatoriosTheme
import com.smartwatch.recordatorios.ui.theme.Sizes
import com.smartwatch.recordatorios.ui.theme.Spacing
import com.smartwatch.recordatorios.ui.theme.timeText

/**
 * Alerta a pantalla completa, la única en coral: Tomi suena sobre el medicamento y «Tomada» va en el
 * botón del borde, siempre a la vista. Posponer y Omitir (con confirmación) quedan debajo, al
 * deslizar, para no tocarlos por error. Tras «Tomada», Tomi salta y se ve la marca de éxito; tras
 * «Posponer», el resumen de pospuestas. Quien aloja la pantalla decide cuándo cerrarla.
 */
@Composable
fun DoseAlertScreen(
    dose: DoseEntity,
    onTake: () -> Unit,
    onSnooze: () -> Unit,
    onSkip: () -> Unit,
    onUndo: (() -> Unit)? = null,
    lastOfDay: Boolean = false,
    onDone: (() -> Unit)? = null,
) {
    var confirmingSkip by rememberSaveable { mutableStateOf(false) }
    var takenAt by rememberSaveable { mutableStateOf<Long?>(null) }
    var snoozedAt by rememberSaveable { mutableStateOf<Long?>(null) }
    val listState = rememberTransformingLazyColumnState()
    // Cada cambio de vista (confirmar omitir, tomada, pospuesta) empieza arriba: si se había
    // desplazado para llegar a Omitir, la confirmación quedaría bajo la hora.
    LaunchedEffect(confirmingSkip, takenAt, snoozedAt) { listState.scrollToItem(0) }
    val pending = dose.status == DoseStatus.SCHEDULED
    val asking = takenAt == null && snoozedAt == null && !confirmingSkip
    val take = {
        takenAt = System.currentTimeMillis()
        onTake()
    }

    // Coral solo mientras pide la toma; lo demás vuelve al fondo claro.
    BwScreenScaffold(
        scrollState = listState,
        tone = if (asking) BwTone.ALERT else BwTone.PLAIN,
        edgeButton = if (asking && pending) ({ TakeEdgeButton(onClick = take) }) else null,
    ) { contentPadding ->
        TransformingLazyColumn(state = listState, contentPadding = contentPadding) {
            val taken = takenAt
            val snoozed = snoozedAt
            when {
                taken != null -> {
                    edgeItem { Tomi(mood = TomiMood.HAPPY) }
                    edgeItem {
                        BwSuccessMark(
                            // La última dosis del día cierra el día: lo dice, sin exclamaciones.
                            label = stringResource(if (lastOfDay) R.string.all_done_today else R.string.status_taken),
                            details =
                                listOf(
                                    stringResource(R.string.taken_detail, dose.medicationName, dose.doseLabel),
                                    stringResource(R.string.taken_at, timeText(taken)),
                                ),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    // Listo vuelve al inicio ya; si no se toca, vuelve solo a los pocos segundos.
                    onDone?.let { done ->
                        edgeItem(container = true) {
                            BwButton(
                                label = stringResource(R.string.alert_done),
                                onClick = done,
                                primary = true,
                                icon = R.drawable.ic_check,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                    // Un toque equivocado en la muñeca se corrige aquí, unos segundos.
                    onUndo?.let { undo ->
                        edgeItem(container = true) {
                            BwButton(
                                label = stringResource(R.string.action_undo),
                                onClick = {
                                    takenAt = null
                                    undo()
                                },
                                icon = R.drawable.ic_undo,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
                snoozed != null -> snoozedSummary(dose, snoozed)
                confirmingSkip -> skipConfirmation(dose, onSkip = onSkip, onBack = { confirmingSkip = false })
                else -> {
                    doseSummary(dose)
                    if (pending) {
                        laterActions(
                            dose,
                            onSnooze = {
                                snoozedAt = System.currentTimeMillis()
                                onSnooze()
                            },
                            onAskSkip = { confirmingSkip = true },
                        )
                    }
                }
            }
        }
    }
}

/** «Tomada» en el botón del borde: blanco sobre el coral, con el visto y una palabra. */
@Composable
private fun TakeEdgeButton(onClick: () -> Unit) {
    BwEdgeButton(
        label = stringResource(R.string.action_take),
        icon = R.drawable.ic_check,
        onClick = onClick,
        container = BwTheme.colors.surface,
        content = BwTheme.colors.ink,
    )
}

private fun TransformingLazyColumnScope.doseSummary(dose: DoseEntity) {
    edgeItem { CenteredText(doseStatusText(dose), style = BwTheme.type.support) }
    edgeItem { Tomi(mood = TomiMood.RINGING) }
    edgeItem {
        MedicationLabel(
            name = dose.medicationName,
            colorKey = dose.colorKey,
            modifier = Modifier.fillMaxWidth(),
        )
    }
    edgeItem { CenteredText(dose.doseLabel) }
}

/** Posponer y Omitir, bajo el pliegue. Omitir en grafito porque no se deshace (pide confirmación). */
private fun TransformingLazyColumnScope.laterActions(
    dose: DoseEntity,
    onSnooze: () -> Unit,
    onAskSkip: () -> Unit,
) {
    val minutes = SnoozePolicy.DELAY.toMinutes().toInt()
    // Las opciones empiezan debajo del botón del borde: no asoman detrás de él al abrir la alerta.
    edgeItem { Spacer(Modifier.height(Sizes.edgeButtonRoom)) }
    if (SnoozePolicy.canSnooze(dose.snoozeCount)) {
        edgeItem(container = true) {
            AlertChip(
                label = pluralStringResource(R.plurals.next_in_minutes, minutes, minutes),
                icon = R.drawable.ic_snooze,
                onClick = onSnooze,
            )
        }
    } else {
        edgeItem { CenteredText(stringResource(R.string.snooze_limit), style = BwTheme.type.support) }
    }
    edgeItem(container = true) {
        AlertChip(
            label = stringResource(R.string.action_skip),
            icon = R.drawable.ic_close,
            onClick = onAskSkip,
            destructive = true,
        )
    }
}

/** Píldora con icono y una o dos palabras: blanca, o grafito para lo que no se deshace. */
@Composable
private fun AlertChip(
    label: String,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
    destructive: Boolean = false,
) {
    val colors = BwTheme.colors
    val container = if (destructive) colors.accent else colors.surface
    val content = if (destructive) colors.onAccent else colors.ink
    Row(
        modifier =
            Modifier
                .fillMaxWidth(if (destructive) NARROW_CHIP else 1f)
                .heightIn(min = Sizes.buttonHeight)
                .clip(BwTheme.shapes.pill)
                .background(container)
                .clickable(role = Role.Button, onClick = onClick)
                .semantics(mergeDescendants = true) {}
                .padding(horizontal = Spacing.lg),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BwIcon(icon, contentDescription = null, color = content, modifier = Modifier.size(Sizes.icon))
        BwText(label, style = BwTheme.type.label, color = content, maxLines = 1)
    }
}

/** Omitir es más estrecho que posponer: la acción que no se deshace ocupa menos. */
private const val NARROW_CHIP = 0.82f

/** Tras posponer: cuántas van, cuándo vuelve a sonar y cuántas quedan. */
private fun TransformingLazyColumnScope.snoozedSummary(
    dose: DoseEntity,
    snoozedAt: Long,
) {
    val count = dose.snoozeCount + 1
    val left = SnoozePolicy.MAX_SNOOZES - count
    // Misma composición que la marca de «Tomada»: insignia, qué pasó en grande y el detalle.
    edgeItem {
        BwSnoozeMark(
            label = stringResource(R.string.snoozed_times, count, SnoozePolicy.MAX_SNOOZES),
            details =
                listOf(
                    stringResource(R.string.taken_detail, dose.medicationName, dose.doseLabel),
                    stringResource(R.string.snoozed_rings_at, timeText(SnoozePolicy.nextAlarmAt(snoozedAt))),
                    if (left > 0) {
                        pluralStringResource(R.plurals.snoozes_left, left, left)
                    } else {
                        stringResource(R.string.snooze_limit)
                    },
                ),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/** Omitir no se deshace: la opción segura va primero y el aviso nombra el medicamento. */
private fun TransformingLazyColumnScope.skipConfirmation(
    dose: DoseEntity,
    onSkip: () -> Unit,
    onBack: () -> Unit,
) {
    edgeItem(container = true) {
        BwBanner(
            icon = R.drawable.ic_warning,
            iconDescription = stringResource(R.string.warning_label),
            title = stringResource(R.string.skip_confirm_title, dose.medicationName),
            body = stringResource(R.string.skip_confirm_body),
            modifier = Modifier.fillMaxWidth(),
        )
    }
    edgeItem(container = true) {
        BwButton(
            label = stringResource(R.string.skip_confirm_no),
            onClick = onBack,
            primary = true,
            icon = R.drawable.ic_back,
            modifier = Modifier.fillMaxWidth(),
        )
    }
    edgeItem(container = true) {
        BwActionPill(
            label = stringResource(R.string.skip_confirm_yes),
            icon = R.drawable.ic_close,
            onClick = onSkip,
            quiet = true,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun CenteredText(
    text: String,
    style: TextStyle = BwTheme.type.body,
) {
    BwText(text = text, style = style, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
}

private val previewDose =
    DoseEntity(
        id = "preview",
        scheduleId = "preview",
        medicationName = "Losartán",
        colorKey = "blue",
        doseLabel = "1 tableta",
        scheduledAt = 0,
        nextAlarmAt = 0,
    )

@WearPreviewDevices
@Composable
private fun DoseAlertScreenPreview() {
    RecordatoriosTheme {
        DoseAlertScreen(dose = previewDose, onTake = {}, onSnooze = {}, onSkip = {})
    }
}
