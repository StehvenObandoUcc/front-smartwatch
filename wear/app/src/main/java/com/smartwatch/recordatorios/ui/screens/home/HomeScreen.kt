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
import com.smartwatch.recordatorios.ui.components.BwCornerButton
import com.smartwatch.recordatorios.ui.components.BwCorners
import com.smartwatch.recordatorios.ui.components.BwDial
import com.smartwatch.recordatorios.ui.components.BwEmptyState
import com.smartwatch.recordatorios.ui.components.BwScreenScaffold
import com.smartwatch.recordatorios.ui.components.BwText
import com.smartwatch.recordatorios.ui.components.MedicationLabel
import com.smartwatch.recordatorios.ui.components.dialMarks
import com.smartwatch.recordatorios.ui.components.edgeItem
import com.smartwatch.recordatorios.ui.components.hourOfDay
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.RecordatoriosTheme
import com.smartwatch.recordatorios.ui.theme.Sizes
import com.smartwatch.recordatorios.ui.theme.Spacing
import com.smartwatch.recordatorios.ui.theme.timeText
import kotlinx.coroutines.delay
import java.time.Duration

/**
 * Primera página: avisos de permisos, próxima dosis y estado del plan. Hoy y las demás son las
 * páginas siguientes (deslizar). Los datos y permisos entran por parámetros. Sin vincular
 * ([planInfo] null) no hay plan ni asistente, y [onAddMedication] deja crear medicamentos aquí.
 */
@Composable
fun HomeScreen(
    next: DoseEntity?,
    planInfo: PlanInfo?,
    exactAlarmsAllowed: Boolean,
    notificationsAllowed: Boolean,
    onRequestExactAlarms: () -> Unit,
    onRequestNotifications: () -> Unit,
    onOpenDose: (DoseEntity) -> Unit,
    onOpenChat: (() -> Unit)?,
    onRetryPlan: () -> Unit,
    onAddMedication: (() -> Unit)? = null,
    now: Long = rememberNow(),
    today: List<DoseEntity> = emptyList(),
) {
    val listState = rememberTransformingLazyColumnState()
    val planStatus = planInfo?.let { planStatusText(it, now) }
    val marks = dialMarks(today, now)
    // El arco va de ahora a la próxima toma, si es dentro de las próximas 24 h.
    val arc = next?.takeIf { it.nextAlarmAt - now in 0..DAY_MILLIS }?.let { hourOfDay(now)..hourOfDay(it.nextAlarmAt) }
    val ask = stringResource(R.string.chat_ask)
    BwScreenScaffold(
        scrollState = listState,
        backdrop = { BwDial(marks = marks, arc = arc, now = hourOfDay(now)) },
        corners = onOpenChat?.let { chat -> BwCorners(start = { BwCornerButton(R.drawable.ic_mic, ask, chat) }) },
    ) { contentPadding ->
        TransformingLazyColumn(
            state = listState,
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            // Avisos arriba del todo: sin estos permisos la alarma puede no sonar.
            if (!notificationsAllowed) {
                edgeItem(container = true) {
                    PermissionWarning(
                        title = stringResource(R.string.permission_notifications_title),
                        body = stringResource(R.string.permission_notifications_body),
                        onClick = onRequestNotifications,
                    )
                }
            }
            if (!exactAlarmsAllowed) {
                edgeItem(container = true) {
                    PermissionWarning(
                        title = stringResource(R.string.permission_exact_title),
                        body = stringResource(R.string.permission_exact_body),
                        onClick = onRequestExactAlarms,
                    )
                }
            }

            // Sin red las alarmas siguen sonando (son locales): se dice para no alarmar y se ofrece reintentar.
            if (planInfo?.status == PlanStatus.OFFLINE) {
                edgeItem(container = true) {
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
                    BwEmptyState(
                        icon = R.drawable.ic_pill,
                        title = stringResource(R.string.home_no_next),
                        body =
                            stringResource(
                                if (onAddMedication !=
                                    null
                                ) {
                                    R.string.home_no_next_local_body
                                } else {
                                    R.string.home_no_next_body
                                },
                            ),
                        modifier = Modifier.fillMaxWidth(),
                    )
                } else {
                    NextDose(dose = next, now = now, onClick = { onOpenDose(next) })
                }
            }

            onAddMedication?.let { add ->
                edgeItem(container = true) {
                    BwButton(
                        label = stringResource(R.string.home_add),
                        onClick = add,
                        primary = next == null,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            planStatus?.let { status ->
                edgeItem {
                    BwText(
                        text = status,
                        style = BwTheme.type.support,
                        color = BwTheme.colors.contentSoft,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
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
            stringResource(R.string.next_label),
            style = BwTheme.type.support,
            color = BwTheme.colors.contentSoft,
            textAlign = TextAlign.Center,
        )
        BwText(
            timeLeft(dose.nextAlarmAt, now),
            style = BwTheme.type.display,
            textAlign = TextAlign.Center,
            maxLines = 1,
            autoSize = BwTheme.type.displayFit,
            // Margen: la cifra ocupa todo el ancho y en la parte alta el círculo es más estrecho.
            // y la escala de la esfera ocupa los lados: la cifra se achica antes que pisarla.
            modifier = Modifier.fillMaxWidth().padding(horizontal = Sizes.dialClearance),
        )
        MedicationLabel(name = dose.medicationName, colorKey = dose.colorKey)
        BwText(
            text = stringResource(R.string.next_detail, timeText(dose.nextAlarmAt), dose.doseLabel),
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
private const val DAY_MILLIS = 24 * 60 * 60 * 1000L
private val TICK = Duration.ofSeconds(TICK_SECONDS)

/**
 * El estado del plan solo cuando pide algo al paciente: sin plan, fallo, falta el consentimiento o
 * caduca en menos de [EXPIRY_NOTICE]. Si todo va bien no se muestra (sería ruido técnico).
 */
@Composable
private fun planStatusText(
    info: PlanInfo,
    now: Long,
): String? =
    when {
        info.status == PlanStatus.CONSENT_REQUIRED -> stringResource(R.string.plan_consent)
        info.status == PlanStatus.FAILED -> stringResource(R.string.plan_failed)
        info.status == PlanStatus.NEVER || info.validUntil <= 0 -> stringResource(R.string.plan_never)
        info.validUntil <= now -> stringResource(R.string.plan_expired)
        info.validUntil - now < EXPIRY_NOTICE.toMillis() ->
            stringResource(R.string.plan_expires, timeText(info.validUntil))
        else -> null
    }

private val EXPIRY_NOTICE = Duration.ofDays(1)

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
