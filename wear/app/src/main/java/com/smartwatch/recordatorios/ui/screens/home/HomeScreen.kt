package com.smartwatch.recordatorios.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
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
import com.smartwatch.recordatorios.ui.components.BwScreenScaffold
import com.smartwatch.recordatorios.ui.components.BwText
import com.smartwatch.recordatorios.ui.components.MedicationLabel
import com.smartwatch.recordatorios.ui.components.edgeItem
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.RecordatoriosTheme
import com.smartwatch.recordatorios.ui.theme.Spacing
import com.smartwatch.recordatorios.ui.theme.formatDateTime
import com.smartwatch.recordatorios.ui.theme.formatTime
import kotlinx.coroutines.delay
import java.time.Duration

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
    now: Long = rememberNow(),
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
                edgeItem {
                    PermissionWarning(
                        title = stringResource(R.string.permission_notifications_title),
                        body = stringResource(R.string.permission_notifications_body),
                        onClick = onRequestNotifications,
                    )
                }
            }
            if (!exactAlarmsAllowed) {
                edgeItem {
                    PermissionWarning(
                        title = stringResource(R.string.permission_exact_title),
                        body = stringResource(R.string.permission_exact_body),
                        onClick = onRequestExactAlarms,
                    )
                }
            }

            // Sin red las alarmas siguen sonando (son locales): se dice para no alarmar y se ofrece reintentar.
            if (planInfo.status == PlanStatus.OFFLINE) {
                edgeItem {
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

            edgeItem {
                if (next == null) {
                    BwText(
                        text = stringResource(R.string.home_no_next),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                } else {
                    NextDose(dose = next, now = now, onClick = { onOpenDose(next) })
                }
            }

            edgeItem {
                BwButton(
                    label = stringResource(R.string.chat_ask),
                    onClick = onOpenChat,
                    icon = R.drawable.ic_mic,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            edgeItem {
                BwText(
                    text = planStatusText(planInfo),
                    style = BwTheme.type.support,
                    color = BwTheme.colors.contentSoft,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

/**
 * La próxima toma sin caja, como protagonista: cuánto falta en grande (no la hora, para no competir
 * con el reloj de arriba), el medicamento con su color y la hora y la dosis en tono suave.
 */
@Composable
private fun NextDose(
    dose: DoseEntity,
    now: Long,
    onClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(BwTheme.shapes.card)
                .clickable(onClickLabel = stringResource(R.string.open_dose), role = Role.Button, onClick = onClick)
                .padding(vertical = Spacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        BwText(
            timeLeft(dose.nextAlarmAt, now),
            style = BwTheme.type.display,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
        MedicationLabel(name = dose.medicationName, colorKey = dose.colorKey)
        BwText(
            text = stringResource(R.string.next_detail, formatTime(dose.nextAlarmAt), dose.doseLabel),
            color = BwTheme.colors.contentSoft,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun timeLeft(
    at: Long,
    now: Long,
): String {
    val left = Duration.ofMillis(at - now)
    return when {
        left <= Duration.ZERO -> stringResource(R.string.next_now)
        left < Duration.ofHours(1) -> {
            val minutes = left.toMinutes().toInt().coerceAtLeast(1)
            pluralStringResource(R.plurals.next_in_minutes, minutes, minutes)
        }
        else -> {
            val hours = left.toHours().toInt()
            pluralStringResource(R.plurals.next_in_hours, hours, hours)
        }
    }
}

/** La hora actual, refrescada cada [TICK] para que «En N min» baje solo. */
@Composable
private fun rememberNow(): Long {
    val now by produceState(System.currentTimeMillis()) {
        while (true) {
            delay(TICK.toMillis())
            value = System.currentTimeMillis()
        }
    }
    return now
}

private const val TICK_SECONDS = 30L
private val TICK = Duration.ofSeconds(TICK_SECONDS)

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
