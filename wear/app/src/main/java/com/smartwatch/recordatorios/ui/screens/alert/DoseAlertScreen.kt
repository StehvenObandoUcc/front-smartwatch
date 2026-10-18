package com.smartwatch.recordatorios.ui.screens.alert

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.smartwatch.recordatorios.R
import com.smartwatch.recordatorios.alarm.SnoozePolicy
import com.smartwatch.recordatorios.data.local.DoseEntity
import com.smartwatch.recordatorios.data.local.DoseStatus
import com.smartwatch.recordatorios.ui.components.BwButton
import com.smartwatch.recordatorios.ui.components.BwScreenScaffold
import com.smartwatch.recordatorios.ui.components.BwText
import com.smartwatch.recordatorios.ui.components.MedicationLabel
import com.smartwatch.recordatorios.ui.components.doseStatusText
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.RecordatoriosTheme

/** Alerta a pantalla completa: Tomada, Posponer y Omitir (con confirmación). */
@Composable
fun DoseAlertScreen(
    dose: DoseEntity,
    onTake: () -> Unit,
    onSnooze: () -> Unit,
    onSkip: () -> Unit,
) {
    var confirmingSkip by rememberSaveable { mutableStateOf(false) }
    val listState = rememberTransformingLazyColumnState()
    val pending = dose.status == DoseStatus.SCHEDULED

    BwScreenScaffold(scrollState = listState) { contentPadding ->
        TransformingLazyColumn(state = listState, contentPadding = contentPadding) {
            item {
                BwText(
                    text = stringResource(if (pending) R.string.alert_title else R.string.alert_done),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item { MedicationLabel(name = dose.medicationName, colorKey = dose.colorKey) }
            item { BwText(dose.doseLabel) }
            item { BwText(doseStatusText(dose)) }

            when {
                !pending -> Unit
                confirmingSkip -> {
                    item {
                        BwText(
                            text = stringResource(R.string.skip_confirm_title),
                            style = BwTheme.type.title,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    item {
                        BwButton(
                            label = stringResource(R.string.skip_confirm_yes),
                            onClick = onSkip,
                            primary = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    item {
                        BwButton(
                            label = stringResource(R.string.skip_confirm_no),
                            onClick = { confirmingSkip = false },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
                else -> {
                    item {
                        BwButton(
                            label = stringResource(R.string.action_take),
                            onClick = onTake,
                            primary = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    if (SnoozePolicy.canSnooze(dose.snoozeCount)) {
                        item {
                            BwButton(
                                label = stringResource(R.string.action_snooze),
                                onClick = onSnooze,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                    item {
                        BwButton(
                            label = stringResource(R.string.action_skip),
                            onClick = { confirmingSkip = true },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
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
