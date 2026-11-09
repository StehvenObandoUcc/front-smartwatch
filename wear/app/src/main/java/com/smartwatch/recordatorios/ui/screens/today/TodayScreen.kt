package com.smartwatch.recordatorios.ui.screens.today

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import com.smartwatch.recordatorios.R
import com.smartwatch.recordatorios.data.local.DoseEntity
import com.smartwatch.recordatorios.data.local.DoseStatus
import com.smartwatch.recordatorios.ui.components.BwDoseRow
import com.smartwatch.recordatorios.ui.components.BwEmptyState
import com.smartwatch.recordatorios.ui.components.BwHeader
import com.smartwatch.recordatorios.ui.components.BwScreenScaffold
import com.smartwatch.recordatorios.ui.components.BwSignBar
import com.smartwatch.recordatorios.ui.components.BwText
import com.smartwatch.recordatorios.ui.components.DayPart
import com.smartwatch.recordatorios.ui.components.DoseStateTone
import com.smartwatch.recordatorios.ui.components.doseStatusLabel
import com.smartwatch.recordatorios.ui.components.edgeItem
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.Spacing
import com.smartwatch.recordatorios.ui.theme.timeText
import java.time.Instant
import java.time.ZoneId

/**
 * Las dosis de hoy por franja (mañana, tarde y noche, como el pastillero del panel web), cada una con
 * la placa de su estado, y cuántas van tomadas.
 */
@Composable
fun TodayScreen(
    doses: List<DoseEntity>?,
    onOpenDose: (DoseEntity) -> Unit,
    now: Long = System.currentTimeMillis(),
) {
    val listState = rememberTransformingLazyColumnState()
    BwScreenScaffold(scrollState = listState) { contentPadding ->
        TransformingLazyColumn(
            state = listState,
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            edgeItem { BwHeader(stringResource(R.string.today_title)) }
            if (!doses.isNullOrEmpty()) {
                edgeItem {
                    BwText(
                        text =
                            stringResource(
                                R.string.today_progress,
                                doses.count { it.status == DoseStatus.TAKEN },
                                doses.size,
                            ),
                        style = BwTheme.type.title,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            when {
                doses == null -> Unit
                doses.isEmpty() ->
                    edgeItem {
                        BwEmptyState(
                            icon = R.drawable.ic_pill,
                            title = stringResource(R.string.today_empty),
                            body = stringResource(R.string.today_empty_body),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                else ->
                    doses.groupBy { dayPartOf(it.scheduledAt) }.toSortedMap().forEach { (part, partDoses) ->
                        edgeItem(key = part.name) {
                            BwSignBar(part, stringResource(part.label()))
                        }
                        partDoses.forEach { dose ->
                            edgeItem(key = dose.id) {
                                DoseRow(dose, now, onClick = { onOpenDose(dose) })
                            }
                        }
                    }
            }
        }
    }
}

/** Una fila por dosis, como en Historial: hora, medicamento y el estado si ya tocó o se resolvió. */
@Composable
private fun DoseRow(
    dose: DoseEntity,
    now: Long,
    onClick: () -> Unit,
) {
    BwDoseRow(
        time = timeText(dose.scheduledAt),
        name = dose.medicationName,
        colorKey = dose.colorKey,
        tone = stateTone(dose, now),
        stateLabel = doseStatusLabel(dose),
        onClickLabel = stringResource(R.string.open_dose),
        onClick = onClick,
    )
}

/** Las mismas franjas que el panel web: 5 a 12 mañana, 12 a 19 tarde, el resto noche. */
private fun dayPartOf(epochMillis: Long): DayPart {
    val hour = Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).hour
    return when (hour) {
        in MORNING_START until AFTERNOON_START -> DayPart.MORNING
        in AFTERNOON_START until NIGHT_START -> DayPart.AFTERNOON
        else -> DayPart.NIGHT
    }
}

private fun DayPart.label(): Int =
    when (this) {
        DayPart.MORNING -> R.string.part_morning
        DayPart.AFTERNOON -> R.string.part_afternoon
        DayPart.NIGHT -> R.string.part_night
    }

/** Placa de estado: tomada u omitida, o pendiente si ya es la hora; las que aún no tocan, sin placa. */
private fun stateTone(
    dose: DoseEntity,
    now: Long,
): DoseStateTone? =
    when {
        dose.status == DoseStatus.TAKEN -> DoseStateTone.TAKEN
        dose.status == DoseStatus.SKIPPED -> DoseStateTone.SKIPPED
        dose.scheduledAt <= now -> DoseStateTone.DUE
        else -> null
    }

private const val MORNING_START = 5
private const val AFTERNOON_START = 12
private const val NIGHT_START = 19
