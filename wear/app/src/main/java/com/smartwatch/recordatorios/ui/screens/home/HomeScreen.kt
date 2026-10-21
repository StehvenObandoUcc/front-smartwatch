package com.smartwatch.recordatorios.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.smartwatch.recordatorios.R
import com.smartwatch.recordatorios.data.local.DoseEntity
import com.smartwatch.recordatorios.data.remote.PlanInfo
import com.smartwatch.recordatorios.data.remote.PlanStatus
import com.smartwatch.recordatorios.ui.components.BwBanner
import com.smartwatch.recordatorios.ui.components.BwButton
import com.smartwatch.recordatorios.ui.components.BwCard
import com.smartwatch.recordatorios.ui.components.BwHeader
import com.smartwatch.recordatorios.ui.components.BwScreenScaffold
import com.smartwatch.recordatorios.ui.components.BwText
import com.smartwatch.recordatorios.ui.components.MedicationLabel
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.RecordatoriosTheme
import com.smartwatch.recordatorios.ui.theme.Spacing
import com.smartwatch.recordatorios.ui.theme.formatDateTime
import com.smartwatch.recordatorios.ui.theme.formatTime

/**
 * Primera página: avisos de permisos, próxima dosis y estado del plan. Hoy y Adherencia son las
 * páginas siguientes (deslizar). Los datos y permisos entran por parámetros.
 */
@Composable
fun HomeScreen(
    next: DoseEntity?,
    planInfo: PlanInfo,
    exactAlarmsAllowed: Boolean,
    notificationsAllowed: Boolean,
    onRequestExactAlarms: () -> Unit,
    onRequestNotifications: () -> Unit,
    onOpenDose: (DoseEntity) -> Unit,
    onOpenChat: () -> Unit,
    onRetryPlan: () -> Unit,
) {
    val listState = rememberTransformingLazyColumnState()
    BwScreenScaffold(scrollState = listState) { contentPadding ->
        TransformingLazyColumn(
            state = listState,
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
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

            // Sin red las alarmas siguen sonando (son locales): se dice para no alarmar y se ofrece reintentar.
            if (planInfo.status == PlanStatus.OFFLINE) {
                item {
                    BwBanner(
                        icon = R.drawable.ic_warning,
                        iconDescription = stringResource(R.string.warning_label),
                        title = stringResource(R.string.plan_offline_title),
                        body = stringResource(R.string.plan_offline_body),
                        onClick = onRetryPlan,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            item { BwHeader(stringResource(R.string.home_title)) }

            item {
                if (next == null) {
                    BwText(
                        text = stringResource(R.string.home_no_next),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                } else {
                    BwCard(
                        onClick = { onOpenDose(next) },
                        onClickLabel = stringResource(R.string.open_dose),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        BwText(stringResource(R.string.home_next), style = BwTheme.type.support)
                        MedicationLabel(name = next.medicationName, colorKey = next.colorKey)
                        BwText(next.doseLabel)
                        BwText(formatTime(next.nextAlarmAt), style = BwTheme.type.title)
                    }
                }
            }

            item {
                BwButton(
                    label = stringResource(R.string.chat_ask),
                    onClick = onOpenChat,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            item {
                BwText(
                    text = planStatusText(planInfo),
                    style = BwTheme.type.support,
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
        PlanStatus.OFFLINE ->
            if (info.validUntil > 0) {
                stringResource(R.string.plan_valid_until, formatDateTime(info.validUntil))
            } else {
                stringResource(R.string.plan_never)
            }
        PlanStatus.CONSENT_REQUIRED -> stringResource(R.string.plan_consent)
        PlanStatus.FAILED -> stringResource(R.string.plan_failed)
    }

/** Aviso de permiso: acento, icono y texto (nunca solo color). */
@Composable
private fun PermissionWarning(
    title: String,
    body: String,
    onClick: () -> Unit,
) {
    BwBanner(
        icon = R.drawable.ic_warning,
        iconDescription = stringResource(R.string.warning_label),
        title = title,
        body = body,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
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
            onOpenChat = {},
            onRetryPlan = {},
        )
    }
}
