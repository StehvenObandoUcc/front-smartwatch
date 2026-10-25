package com.smartwatch.recordatorios.ui.screens.alert

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import com.smartwatch.recordatorios.ui.components.BwIcon
import com.smartwatch.recordatorios.ui.components.BwScreenScaffold
import com.smartwatch.recordatorios.ui.components.BwSnoozeMark
import com.smartwatch.recordatorios.ui.components.BwSuccessMark
import com.smartwatch.recordatorios.ui.components.BwText
import com.smartwatch.recordatorios.ui.components.BwTone
import com.smartwatch.recordatorios.ui.components.MedicationLabel
import com.smartwatch.recordatorios.ui.components.doseStatusText
import com.smartwatch.recordatorios.ui.components.edgeItem
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.RecordatoriosTheme
import com.smartwatch.recordatorios.ui.theme.Sizes
import com.smartwatch.recordatorios.ui.theme.Spacing
import com.smartwatch.recordatorios.ui.theme.timeText

/**
 * Alerta a pantalla completa: Tomada, Posponer y Omitir (con confirmación). Todo centrado y sin
 * título aparte: el estado ("08:00 · Pendiente") hace de título para que «Tomada» quepa sin desplazarse.
 * Tras «Tomada» se muestra la marca de éxito y tras «Posponer» el resumen de pospuestas; quien aloja
 * la pantalla decide cuándo cerrarla.
 */
@Composable
fun DoseAlertScreen(
    dose: DoseEntity,
    onTake: () -> Unit,
    onSnooze: () -> Unit,
    onSkip: () -> Unit,
    onUndo: (() -> Unit)? = null,
    lastOfDay: Boolean = false,
) {
    var confirmingSkip by rememberSaveable { mutableStateOf(false) }
    var takenAt by rememberSaveable { mutableStateOf<Long?>(null) }
    var snoozedAt by rememberSaveable { mutableStateOf<Long?>(null) }
    val listState = rememberTransformingLazyColumnState()
    // Cada cambio de vista (confirmar omitir, tomada, pospuesta) empieza arriba: si se había
    // desplazado para llegar a Omitir, la confirmación quedaría bajo la hora.
    LaunchedEffect(confirmingSkip, takenAt, snoozedAt) { listState.scrollToItem(0) }
    val pending = dose.status == DoseStatus.SCHEDULED

    // Naranja rojizo mientras pide algo; verde cuando ya se tomó.
    val tone = if (takenAt != null) BwTone.DONE else BwTone.ALERT
    BwScreenScaffold(scrollState = listState, tone = tone) { contentPadding ->
        TransformingLazyColumn(state = listState, contentPadding = contentPadding) {
            val taken = takenAt
            val snoozed = snoozedAt
            when {
                taken != null -> {
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
                        pendingActions(
                            dose,
                            onTake = {
                                takenAt = System.currentTimeMillis()
                                onTake()
                            },
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

private fun TransformingLazyColumnScope.doseSummary(dose: DoseEntity) {
    edgeItem { CenteredText(doseStatusText(dose), style = BwTheme.type.support) }
    edgeItem {
        MedicationLabel(
            name = dose.medicationName,
            colorKey = dose.colorKey,
            modifier = Modifier.fillMaxWidth(),
        )
    }
    edgeItem { CenteredText(dose.doseLabel) }
}

private fun TransformingLazyColumnScope.pendingActions(
    dose: DoseEntity,
    onTake: () -> Unit,
    onSnooze: () -> Unit,
    onAskSkip: () -> Unit,
) {
    val canSnooze = SnoozePolicy.canSnooze(dose.snoozeCount)
    if (!canSnooze) {
        edgeItem {
            BwText(
                text = stringResource(R.string.snooze_limit),
                style = BwTheme.type.support,
                color = BwTheme.colors.contentSoft,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
    // Tres discos en una fila: Tomada, el grande y claro, en el centro; Omitir y Posponer, de vidrio,
    // a los lados (Omitir pide confirmación). Sin más pospuestas, Posponer no aparece.
    edgeItem(container = true) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.Top,
        ) {
            ActionDisc(stringResource(R.string.action_skip), R.drawable.ic_close, onClick = onAskSkip)
            ActionDisc(stringResource(R.string.action_take), R.drawable.ic_check, onClick = onTake, main = true)
            if (canSnooze) {
                ActionDisc(
                    stringResource(R.string.snooze_short),
                    R.drawable.ic_snooze,
                    onClick = onSnooze,
                    detail = stringResource(R.string.snooze_minutes, SnoozePolicy.DELAY.toMinutes()),
                )
            }
        }
    }
}

/** Una acción como disco con su palabra debajo; disco y palabra son un solo botón para TalkBack. */
@Composable
private fun ActionDisc(
    label: String,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
    main: Boolean = false,
    detail: String? = null,
) {
    val colors = BwTheme.colors
    Column(
        modifier =
            Modifier
                .clip(BwTheme.shapes.card)
                .clickable(role = Role.Button, onClick = onClick)
                .semantics(mergeDescendants = true) {}
                .padding(vertical = Spacing.xs),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Box(
            modifier =
                Modifier
                    .size(if (main) Sizes.discMain else Sizes.discSecondary)
                    .clip(CircleShape)
                    .background(if (main) colors.primary else colors.surface),
            contentAlignment = Alignment.Center,
        ) {
            BwIcon(
                icon,
                contentDescription = null,
                color = if (main) colors.onPrimary else colors.content,
                modifier = Modifier.size(Sizes.discIcon),
            )
        }
        BwText(label, style = BwTheme.type.support, textAlign = TextAlign.Center, maxLines = 1)
        detail?.let { BwText(it, style = BwTheme.type.support, color = colors.contentSoft, maxLines = 1) }
    }
}

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
