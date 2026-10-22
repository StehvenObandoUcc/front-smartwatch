package com.smartwatch.recordatorios.ui.screens.alert

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
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
import com.smartwatch.recordatorios.ui.components.BwScreenScaffold
import com.smartwatch.recordatorios.ui.components.BwSnoozeMark
import com.smartwatch.recordatorios.ui.components.BwSuccessMark
import com.smartwatch.recordatorios.ui.components.BwText
import com.smartwatch.recordatorios.ui.components.MedicationLabel
import com.smartwatch.recordatorios.ui.components.doseStatusText
import com.smartwatch.recordatorios.ui.components.edgeItem
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.RecordatoriosTheme
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
) {
    var confirmingSkip by rememberSaveable { mutableStateOf(false) }
    var takenAt by rememberSaveable { mutableStateOf<Long?>(null) }
    var snoozedAt by rememberSaveable { mutableStateOf<Long?>(null) }
    val listState = rememberTransformingLazyColumnState()
    // Cada cambio de vista (confirmar omitir, tomada, pospuesta) empieza arriba: si se había
    // desplazado para llegar a Omitir, la confirmación quedaría bajo la hora.
    LaunchedEffect(confirmingSkip, takenAt, snoozedAt) { listState.scrollToItem(0) }
    val pending = dose.status == DoseStatus.SCHEDULED

    BwScreenScaffold(scrollState = listState) { contentPadding ->
        TransformingLazyColumn(state = listState, contentPadding = contentPadding) {
            val taken = takenAt
            val snoozed = snoozedAt
            when {
                taken != null ->
                    edgeItem {
                        BwSuccessMark(
                            label = stringResource(R.string.status_taken),
                            details =
                                listOf(
                                    stringResource(R.string.taken_detail, dose.medicationName, dose.doseLabel),
                                    stringResource(R.string.taken_at, timeText(taken)),
                                ),
                            modifier = Modifier.fillMaxWidth(),
                        )
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
    edgeItem(container = true) {
        BwButton(
            label = stringResource(R.string.action_take),
            onClick = onTake,
            primary = true,
            icon = R.drawable.ic_check,
            modifier = Modifier.fillMaxWidth(),
        )
    }
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
    // Posponer (neutra) y Omitir (solo contorno) lado a lado, más estrechas que la acción principal:
    // la parte baja de la pantalla redonda es más angosta.
    edgeItem(container = true) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.md),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            if (canSnooze) {
                BwActionPill(
                    label = stringResource(R.string.snooze_short),
                    icon = R.drawable.ic_snooze,
                    detail = stringResource(R.string.snooze_minutes, SnoozePolicy.DELAY.toMinutes()),
                    onClick = onSnooze,
                    modifier = Modifier.weight(1f),
                )
            }
            BwActionPill(
                label = stringResource(R.string.action_skip),
                icon = R.drawable.ic_close,
                onClick = onAskSkip,
                quiet = true,
                modifier = Modifier.weight(1f),
            )
        }
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
