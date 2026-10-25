package com.smartwatch.recordatorios.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import com.smartwatch.recordatorios.R
import com.smartwatch.recordatorios.data.local.DoseEntity
import com.smartwatch.recordatorios.data.local.DoseStatus
import com.smartwatch.recordatorios.ui.components.BwEmptyState
import com.smartwatch.recordatorios.ui.components.BwIcon
import com.smartwatch.recordatorios.ui.components.BwScreenScaffold
import com.smartwatch.recordatorios.ui.components.BwText
import com.smartwatch.recordatorios.ui.components.MedicationLabel
import com.smartwatch.recordatorios.ui.components.edgeItem
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.Sizes
import com.smartwatch.recordatorios.ui.theme.Spacing
import com.smartwatch.recordatorios.ui.theme.timeText
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

/** Cómo quedó una toma pasada en el historial. */
enum class HistoryState { TAKEN, SKIPPED, MISSED, DUE }

/** Una toma del historial con su día local. */
data class HistoryEntry(
    val dose: DoseEntity,
    val day: LocalDate,
    val state: HistoryState,
)

/**
 * Las tomas que ya llegaron a su hora en los últimos [days] días, de la más reciente a la más
 * antigua. Una sin registrar pasa a «No tomada» a los 60 min de su hora (como el backend); antes, es
 * «Pendiente».
 */
fun historyEntries(
    doses: List<DoseEntity>,
    now: Long,
    zone: ZoneId = ZoneId.systemDefault(),
    days: Long = HISTORY_DAYS,
): List<HistoryEntry> {
    val today = LocalDate.ofInstant(Instant.ofEpochMilli(now), zone)
    val first = today.minusDays(days - 1)
    return doses
        .filter { it.scheduledAt <= now }
        .map { dose ->
            val day = LocalDate.ofInstant(Instant.ofEpochMilli(dose.scheduledAt), zone)
            val state =
                when {
                    dose.status == DoseStatus.TAKEN -> HistoryState.TAKEN
                    dose.status == DoseStatus.SKIPPED -> HistoryState.SKIPPED
                    now - dose.scheduledAt >= MISSED_AFTER_MILLIS -> HistoryState.MISSED
                    else -> HistoryState.DUE
                }
            HistoryEntry(dose, day, state)
        }.filter { !it.day.isBefore(first) }
        .sortedByDescending { it.dose.scheduledAt }
}

/**
 * Historial de 7 días: cada toma con la hora a la izquierda (cifras tabulares), el medicamento con su
 * placa y el icono de su estado a la derecha, agrupado por día (Hoy, Ayer, el día de la semana).
 */
@Composable
fun HistoryScreen(
    entries: List<HistoryEntry>?,
    today: LocalDate = LocalDate.now(),
) {
    val listState = rememberTransformingLazyColumnState()
    BwScreenScaffold(scrollState = listState) { contentPadding ->
        TransformingLazyColumn(
            state = listState,
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            edgeItem {
                BwText(
                    stringResource(R.string.history_title),
                    style = BwTheme.type.title,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            when {
                entries == null -> Unit
                entries.isEmpty() ->
                    edgeItem {
                        BwEmptyState(
                            icon = R.drawable.ic_chart,
                            title = stringResource(R.string.history_empty),
                            body = stringResource(R.string.history_empty_body),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                else ->
                    entries.groupBy { it.day }.forEach { (day, dayEntries) ->
                        edgeItem(key = day.toString()) {
                            BwText(
                                dayLabel(day, today),
                                style = BwTheme.type.support,
                                color = BwTheme.colors.contentSoft,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        dayEntries.forEach { entry ->
                            edgeItem(key = entry.dose.id) { HistoryRow(entry) }
                        }
                    }
            }
        }
    }
}

@Composable
private fun HistoryRow(entry: HistoryEntry) {
    val colors = BwTheme.colors
    val (fill, icon, word) =
        when (entry.state) {
            HistoryState.TAKEN -> Triple(colors.stateTaken, R.drawable.ic_check, R.string.status_taken)
            HistoryState.SKIPPED -> Triple(colors.stateSkipped, R.drawable.ic_skipped, R.string.status_skipped)
            HistoryState.MISSED -> Triple(colors.stateMissed, R.drawable.ic_close, R.string.status_missed)
            HistoryState.DUE -> Triple(colors.stateDue, R.drawable.ic_snooze, R.string.status_scheduled)
        }
    val stateWord = stringResource(word)
    Row(
        modifier = Modifier.fillMaxWidth().semantics(mergeDescendants = true) {},
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        BwText(
            timeText(entry.dose.scheduledAt),
            style = BwTheme.type.label.merge(fontFeatureSettings = "tnum"),
        )
        MedicationLabel(
            name = entry.dose.medicationName,
            colorKey = entry.dose.colorKey,
            centered = false,
            maxLines = 1,
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier.size(Sizes.historyIcon).clip(CircleShape).background(fill),
            contentAlignment = Alignment.Center,
        ) {
            BwIcon(
                icon,
                contentDescription = stateWord,
                color = colors.ink,
                modifier = Modifier.size(Sizes.calendarIcon),
            )
        }
    }
}

@Composable
private fun dayLabel(
    day: LocalDate,
    today: LocalDate,
): String =
    when (day) {
        today -> stringResource(R.string.history_today)
        today.minusDays(1) -> stringResource(R.string.history_yesterday)
        else ->
            day.dayOfWeek.getDisplayName(TextStyle.FULL, SPANISH).replaceFirstChar { it.uppercase() } +
                " ${day.dayOfMonth}"
    }

private val SPANISH = Locale.forLanguageTag("es")
private const val HISTORY_DAYS = 7L
private const val MISSED_AFTER_MILLIS = 60 * 60 * 1000L
