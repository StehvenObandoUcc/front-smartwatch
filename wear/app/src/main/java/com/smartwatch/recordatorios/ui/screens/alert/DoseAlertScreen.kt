package com.smartwatch.recordatorios.ui.screens.alert

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import com.smartwatch.recordatorios.ui.components.BwBanner
import com.smartwatch.recordatorios.ui.components.BwButton
import com.smartwatch.recordatorios.ui.components.BwScreenScaffold
import com.smartwatch.recordatorios.ui.components.BwSuccessMark
import com.smartwatch.recordatorios.ui.components.BwText
import com.smartwatch.recordatorios.ui.components.MedicationLabel
import com.smartwatch.recordatorios.ui.components.doseStatusText
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.RecordatoriosTheme

/**
 * Alerta a pantalla completa: Tomada, Posponer y Omitir (con confirmación). Todo centrado y sin
 * título aparte: el estado ("08:00 · Pendiente") hace de título para que «Tomada» quepa sin desplazarse.
 * Tras «Tomada» se muestra la marca de éxito; quien aloja la pantalla decide cuándo cerrarla.
 */
@Composable
fun DoseAlertScreen(
    dose: DoseEntity,
    onTake: () -> Unit,
    onSnooze: () -> Unit,
    onSkip: () -> Unit,
) {
    var confirmingSkip by rememberSaveable { mutableStateOf(false) }
    var taken by rememberSaveable { mutableStateOf(false) }
    val listState = rememberTransformingLazyColumnState()
    val pending = dose.status == DoseStatus.SCHEDULED

    BwScreenScaffold(scrollState = listState) { contentPadding ->
        TransformingLazyColumn(state = listState, contentPadding = contentPadding) {
            when {
                taken ->
                    item {
                        BwSuccessMark(stringResource(R.string.status_taken), modifier = Modifier.fillMaxWidth())
                    }
                confirmingSkip -> skipConfirmation(dose, onSkip = onSkip, onBack = { confirmingSkip = false })
                else -> {
                    doseSummary(dose)
                    if (pending) {
                        pendingActions(
                            dose,
                            onTake = {
                                taken = true
                                onTake()
                            },
                            onSnooze = onSnooze,
                            onAskSkip = { confirmingSkip = true },
                        )
                    }
                }
            }
        }
    }
}

private fun TransformingLazyColumnScope.doseSummary(dose: DoseEntity) {
    item { CenteredText(doseStatusText(dose)) }
    item { MedicationLabel(name = dose.medicationName, colorKey = dose.colorKey, modifier = Modifier.fillMaxWidth()) }
    item { CenteredText(dose.doseLabel) }
}

private fun TransformingLazyColumnScope.pendingActions(
    dose: DoseEntity,
    onTake: () -> Unit,
    onSnooze: () -> Unit,
    onAskSkip: () -> Unit,
) {
    item {
        BwButton(
            label = stringResource(R.string.action_take),
            onClick = onTake,
            primary = true,
            modifier = Modifier.fillMaxWidth(),
        )
    }
    item {
        if (SnoozePolicy.canSnooze(dose.snoozeCount)) {
            BwButton(
                label = stringResource(R.string.snooze_for, SnoozePolicy.DELAY.toMinutes()),
                onClick = onSnooze,
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            CenteredText(stringResource(R.string.snooze_limit), style = BwTheme.type.support)
        }
    }
    item {
        BwButton(
            label = stringResource(R.string.action_skip),
            onClick = onAskSkip,
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
    item {
        BwBanner(
            icon = R.drawable.ic_warning,
            iconDescription = stringResource(R.string.warning_label),
            title = stringResource(R.string.skip_confirm_title, dose.medicationName),
            body = stringResource(R.string.skip_confirm_body),
            modifier = Modifier.fillMaxWidth(),
        )
    }
    item {
        BwButton(
            label = stringResource(R.string.skip_confirm_no),
            onClick = onBack,
            primary = true,
            modifier = Modifier.fillMaxWidth(),
        )
    }
    item {
        BwButton(
            label = stringResource(R.string.skip_confirm_yes),
            onClick = onSkip,
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
