package com.smartwatch.recordatorios.ui.screens.adherence

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.smartwatch.recordatorios.R
import com.smartwatch.recordatorios.data.remote.AdherenceDay
import com.smartwatch.recordatorios.data.remote.AdherenceSummary
import com.smartwatch.recordatorios.ui.components.BwEmptyState
import com.smartwatch.recordatorios.ui.components.BwIcon
import com.smartwatch.recordatorios.ui.components.BwScreenScaffold
import com.smartwatch.recordatorios.ui.components.BwText
import com.smartwatch.recordatorios.ui.components.edgeItem
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.RecordatoriosTheme
import com.smartwatch.recordatorios.ui.theme.Sizes
import com.smartwatch.recordatorios.ui.theme.Spacing
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

/**
 * Adherencia del mes como calendario: el mes y el porcentaje arriba y una casilla por día con el
 * icono de su resultado (visto, aspa o raya), nunca solo color. Sin números de día: a 14 sp no caben
 * siete por fila. TalkBack lee un resumen en palabras en lugar de cada casilla.
 */
@Composable
fun AdherenceScreen(
    summary: AdherenceSummary?,
    today: LocalDate = LocalDate.now(),
) {
    val listState = rememberTransformingLazyColumnState()
    BwScreenScaffold(scrollState = listState) { contentPadding ->
        TransformingLazyColumn(state = listState, contentPadding = contentPadding) {
            if (summary == null) {
                edgeItem {
                    BwEmptyState(
                        icon = R.drawable.ic_chart,
                        title = stringResource(R.string.adherence_empty),
                        body = stringResource(R.string.adherence_empty_body),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            } else {
                val month =
                    summary.from.month
                        .getDisplayName(
                            TextStyle.FULL,
                            SPANISH,
                        ).replaceFirstChar { it.uppercase() }
                edgeItem {
                    val percent = summary.percentage?.let { stringResource(R.string.adherence_value, it) } ?: "—"
                    BwText(
                        stringResource(R.string.adherence_month, month, percent),
                        style = BwTheme.type.title,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                edgeItem { MonthGrid(summary, today) }
                if (summary.percentage == null) {
                    edgeItem {
                        BwText(
                            stringResource(R.string.adherence_none_month),
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
}

@Composable
private fun MonthGrid(
    summary: AdherenceSummary,
    today: LocalDate,
) {
    val results = summary.days.map(::dayResult)
    val description =
        stringResource(
            R.string.adherence_month_summary,
            results.count { it == DayResult.ALL_TAKEN },
            results.count { it == DayResult.SOME_MISSED },
            results.count { it == DayResult.NO_DOSES },
        )
    // Lunes primero: las casillas vacías antes del día 1 lo alinean con su columna.
    val lead = summary.from.dayOfWeek.value - 1
    val cells: List<Int?> = List(lead) { null } + summary.days.indices.toList()
    Column(
        modifier = Modifier.semantics(mergeDescendants = true) { contentDescription = description },
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs), modifier = Modifier.clearAndSetSemantics {}) {
            WEEKDAYS.forEach {
                BwText(
                    it,
                    style = BwTheme.type.support,
                    color = BwTheme.colors.contentSoft,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.size(Sizes.calendarCell),
                )
            }
        }
        cells.chunked(DAYS_PER_WEEK).forEach { week ->
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs), modifier = Modifier.clearAndSetSemantics {}) {
                week.forEach { index ->
                    if (index == null) {
                        Spacer(Modifier.size(Sizes.calendarCell))
                    } else {
                        DayCell(results[index], isToday = summary.from.plusDays(index.toLong()) == today)
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    result: DayResult,
    isToday: Boolean,
) {
    val colors = BwTheme.colors
    val (fill, icon, tint) =
        when (result) {
            DayResult.ALL_TAKEN -> Triple(colors.stateTaken, R.drawable.ic_check, colors.ink)
            DayResult.SOME_MISSED -> Triple(colors.stateMissed, R.drawable.ic_close, colors.ink)
            DayResult.NO_DOSES -> Triple(colors.surface, R.drawable.ic_skipped, colors.content)
        }
    Box(
        modifier =
            Modifier
                .size(Sizes.calendarCell)
                .clip(CircleShape)
                .background(fill)
                .then(if (isToday) Modifier.border(Sizes.todayRing, colors.content, CircleShape) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        BwIcon(icon, contentDescription = null, color = tint, modifier = Modifier.size(Sizes.calendarIcon))
    }
}

private val SPANISH = Locale.forLanguageTag("es")
private val WEEKDAYS = listOf("L", "M", "X", "J", "V", "S", "D")
private const val DAYS_PER_WEEK = 7

@Suppress("MagicNumber")
@WearPreviewDevices
@Composable
private fun AdherenceScreenPreview() {
    RecordatoriosTheme {
        AdherenceScreen(
            AdherenceSummary(
                percentage = 92,
                from = LocalDate.of(2026, 10, 1),
                days = List(24) { AdherenceDay(taken = if (it == 5) 1 else 2, total = if (it == 10) 0 else 2) },
            ),
            today = LocalDate.of(2026, 10, 24),
        )
    }
}
