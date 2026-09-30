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
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.FilledTonalButton
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.OutlinedButton
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.smartwatch.recordatorios.R
import com.smartwatch.recordatorios.alarm.SnoozePolicy
import com.smartwatch.recordatorios.data.local.DoseEntity
import com.smartwatch.recordatorios.data.local.DoseStatus
import com.smartwatch.recordatorios.data.repository.DemoDoses
import com.smartwatch.recordatorios.ui.components.MedicationLabel
import com.smartwatch.recordatorios.ui.components.doseStatusText
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

    ScreenScaffold(scrollState = listState) { contentPadding ->
        TransformingLazyColumn(state = listState, contentPadding = contentPadding) {
            item {
                Text(
                    text = stringResource(if (pending) R.string.alert_title else R.string.alert_done),
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item { MedicationLabel(name = dose.medicationName, colorKey = dose.colorKey) }
            item { Text(dose.doseLabel, style = MaterialTheme.typography.bodyLarge) }
            item { Text(doseStatusText(dose), style = MaterialTheme.typography.bodyMedium) }

            when {
                !pending -> Unit
                confirmingSkip -> {
                    item {
                        Text(
                            text = stringResource(R.string.skip_confirm_title),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    item {
                        Button(
                            onClick = onSkip,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.skip_confirm_yes)) },
                        )
                    }
                    item {
                        FilledTonalButton(
                            onClick = { confirmingSkip = false },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.skip_confirm_no)) },
                        )
                    }
                }
                else -> {
                    item {
                        Button(
                            onClick = onTake,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.action_take)) },
                        )
                    }
                    if (SnoozePolicy.canSnooze(dose.snoozeCount)) {
                        item {
                            FilledTonalButton(
                                onClick = onSnooze,
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text(stringResource(R.string.action_snooze)) },
                            )
                        }
                    }
                    item {
                        OutlinedButton(
                            onClick = { confirmingSkip = true },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.action_skip)) },
                        )
                    }
                }
            }
        }
    }
}

@WearPreviewDevices
@Composable
private fun DoseAlertScreenPreview() {
    RecordatoriosTheme {
        DoseAlertScreen(dose = DemoDoses.create(0).first(), onTake = {}, onSnooze = {}, onSkip = {})
    }
}
