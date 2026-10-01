package com.smartwatch.recordatorios.ui.screens.home

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.Card
import androidx.wear.compose.material3.FilledTonalButton
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.smartwatch.recordatorios.R
import com.smartwatch.recordatorios.data.local.DoseEntity
import com.smartwatch.recordatorios.data.remote.PlanInfo
import com.smartwatch.recordatorios.data.remote.PlanStatus
import com.smartwatch.recordatorios.ui.components.MedicationLabel
import com.smartwatch.recordatorios.ui.theme.RecordatoriosTheme
import com.smartwatch.recordatorios.ui.theme.formatDateTime
import com.smartwatch.recordatorios.ui.theme.formatTime

/** Inicio: avisos de permisos, próxima dosis y estado del plan. Los datos y permisos entran por parámetros. */
@Composable
fun HomeScreen(
    next: DoseEntity?,
    planInfo: PlanInfo,
    exactAlarmsAllowed: Boolean,
    notificationsAllowed: Boolean,
    onRequestExactAlarms: () -> Unit,
    onRequestNotifications: () -> Unit,
    onOpenDose: (DoseEntity) -> Unit,
    onOpenToday: () -> Unit,
) {
    val listState = rememberTransformingLazyColumnState()
    ScreenScaffold(scrollState = listState) { contentPadding ->
        TransformingLazyColumn(state = listState, contentPadding = contentPadding) {
            // Avisos arriba del todo: sin estos permisos la alarma puede no sonar.
            if (!notificationsAllowed) {
                item {
                    PermissionWarning(
                        title = stringResource(R.string.permission_notifications_title),
                        body = stringResource(R.string.permission_notifications_body),
                        onClick = onRequestNotifications,
                    )
                }
            }
            if (!exactAlarmsAllowed) {
                item {
                    PermissionWarning(
                        title = stringResource(R.string.permission_exact_title),
                        body = stringResource(R.string.permission_exact_body),
                        onClick = onRequestExactAlarms,
                    )
                }
            }

            item { ListHeader { Text(stringResource(R.string.home_title)) } }

            item {
                if (next == null) {
                    Text(
                        text = stringResource(R.string.home_no_next),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                } else {
                    Card(onClick = { onOpenDose(next) }, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.home_next), style = MaterialTheme.typography.labelMedium)
                        MedicationLabel(name = next.medicationName, colorKey = next.colorKey)
                        Text(next.doseLabel, style = MaterialTheme.typography.bodyMedium)
                        Text(formatTime(next.nextAlarmAt), style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            item {
                FilledTonalButton(
                    onClick = onOpenToday,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.home_today_button)) },
                )
            }

            item {
                Text(
                    text = planStatusText(planInfo),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun planStatusText(info: PlanInfo): String =
    when (info.status) {
        PlanStatus.NEVER -> stringResource(R.string.plan_never)
        PlanStatus.OK -> stringResource(R.string.plan_valid_until, formatDateTime(info.validUntil))
        PlanStatus.CONSENT_REQUIRED -> stringResource(R.string.plan_consent)
        PlanStatus.FAILED -> stringResource(R.string.plan_failed)
    }

/** Aviso de permiso: color de error, icono y texto (nunca solo color). */
@Composable
private fun PermissionWarning(
    title: String,
    body: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
                secondaryContentColor = MaterialTheme.colorScheme.onError,
            ),
        icon = { Text(stringResource(R.string.warning_icon), style = MaterialTheme.typography.titleLarge) },
        label = { Text(title, maxLines = 2) },
        secondaryLabel = { Text(body, maxLines = 4) },
    )
}

@WearPreviewDevices
@Composable
private fun HomeScreenPreview() {
    RecordatoriosTheme {
        HomeScreen(
            next =
                DoseEntity(
                    id = "preview",
                    scheduleId = "preview",
                    medicationName = "Losartán",
                    colorKey = "blue",
                    doseLabel = "1 tableta",
                    scheduledAt = 0,
                    nextAlarmAt = 0,
                ),
            planInfo = PlanInfo(PlanStatus.OK, 0),
            exactAlarmsAllowed = false,
            notificationsAllowed = true,
            onRequestExactAlarms = {},
            onRequestNotifications = {},
            onOpenDose = {},
            onOpenToday = {},
        )
    }
}
