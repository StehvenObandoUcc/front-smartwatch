package com.smartwatch.recordatorios.ui.screens.home

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.Card
import androidx.wear.compose.material3.FilledTonalButton
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.smartwatch.recordatorios.R
import com.smartwatch.recordatorios.data.local.DoseEntity
import com.smartwatch.recordatorios.data.repository.DemoDoses
import com.smartwatch.recordatorios.ui.components.MedicationLabel
import com.smartwatch.recordatorios.ui.components.doseStatusText
import com.smartwatch.recordatorios.ui.theme.RecordatoriosTheme

/** Pantalla presentacional: los datos y permisos entran por parámetros. */
@Composable
fun HomeScreen(
    doses: List<DoseEntity>?,
    exactAlarmsAllowed: Boolean,
    notificationsAllowed: Boolean,
    onRequestExactAlarms: () -> Unit,
    onRequestNotifications: () -> Unit,
    onOpenDose: (DoseEntity) -> Unit,
    onResetDemo: () -> Unit,
) {
    val listState = rememberTransformingLazyColumnState()
    ScreenScaffold(scrollState = listState) { contentPadding ->
        TransformingLazyColumn(state = listState, contentPadding = contentPadding) {
            item { ListHeader { Text(stringResource(R.string.home_title)) } }

            if (!notificationsAllowed) {
                item {
                    PermissionButton(
                        title = stringResource(R.string.permission_notifications_title),
                        body = stringResource(R.string.permission_notifications_body),
                        onClick = onRequestNotifications,
                    )
                }
            }
            if (!exactAlarmsAllowed) {
                item {
                    PermissionButton(
                        title = stringResource(R.string.permission_exact_title),
                        body = stringResource(R.string.permission_exact_body),
                        onClick = onRequestExactAlarms,
                    )
                }
            }

            when {
                doses == null -> Unit
                doses.isEmpty() ->
                    item {
                        Text(
                            text = stringResource(R.string.home_empty),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                else ->
                    doses.forEach { dose ->
                        item(key = dose.id) {
                            Card(onClick = { onOpenDose(dose) }, modifier = Modifier.fillMaxWidth()) {
                                MedicationLabel(name = dose.medicationName, colorKey = dose.colorKey)
                                Text(dose.doseLabel, style = MaterialTheme.typography.bodyMedium)
                                Text(doseStatusText(dose), style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
            }

            item {
                FilledTonalButton(
                    onClick = onResetDemo,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.home_reset_demo)) },
                    secondaryLabel = { Text(stringResource(R.string.home_reset_demo_hint)) },
                )
            }
        }
    }
}

@Composable
private fun PermissionButton(
    title: String,
    body: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(title) },
        secondaryLabel = { Text(body, maxLines = 3) },
    )
}

@WearPreviewDevices
@Composable
private fun HomeScreenPreview() {
    RecordatoriosTheme {
        HomeScreen(
            doses = DemoDoses.create(0),
            exactAlarmsAllowed = false,
            notificationsAllowed = true,
            onRequestExactAlarms = {},
            onRequestNotifications = {},
            onOpenDose = {},
            onResetDemo = {},
        )
    }
}
