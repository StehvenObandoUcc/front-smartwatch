package com.smartwatch.recordatorios.ui.screens.adherence

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
 * El mes de un vistazo: el porcentaje en grande («14 %», «tomadas en noviembre») y
 * debajo los últimos 7 días, un punto por día con su letra y el icono de su resultado (visto, aspa o
 * raya), nunca solo color; hoy lleva aro. TalkBack lee un resumen en palabras.
 */
@Composable
fun AdherenceScreen(
    summary: AdherenceSummary?,
    today: LocalDate = LocalDate.now(),
) {
    val listState = rememberTransformingLazyColumnState()
    BwScreenScaffold(scrollState = listState) { contentPadding ->
        TransformingLazyColumn(
            state = listState,
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
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
                val month = summary.from.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                val caption =
                    if (summary.percentage == null) R.string.adherence_none_month else R.string.adherence_taken_month
                edgeItem {
                    BwText(
                        summary.percentage?.let { stringResource(R.string.adherence_value, it) } ?: "—",
                        style = BwTheme.type.display,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                edgeItem {
                    BwText(
                        stringResource(caption, month),
                        style = BwTheme.type.support,
                        color = BwTheme.colors.contentSoft,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                edgeItem { LastWeek(summary, today) }
            }
        }
    }
}

/** Los últimos 7 días hasta hoy; los que caen antes del mes van sin resultado. */
@Composable
private fun LastWeek(
    summary: AdherenceSummary,
    today: LocalDate,
) {
    val week =
        (WEEK - 1 downTo 0).map { back ->
            val day = today.minusDays(back.toLong())
            val index =
                java.time.temporal.ChronoUnit.DAYS
                    .between(summary.from, day)
                    .toInt()
            day to summary.days.getOrNull(index)?.let(::dayResult)
        }
    val results = week.mapNotNull { it.second }
    val description =
        stringResource(
            R.string.adherence_month_summary,
            results.count { it == DayResult.ALL_TAKEN },
            results.count { it == DayResult.SOME_MISSED },
            results.count { it == DayResult.NO_DOSES },
        )
    Row(
        modifier =
            Modifier
                .padding(top = Spacing.sm)
                .semantics(mergeDescendants = true) { contentDescription = description },
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs, Alignment.CenterHorizontally),
    ) {
        week.forEach { (day, result) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                modifier = Modifier.clearAndSetSemantics {},
            ) {
                BwText(
                    // Inicial del día en el idioma de la app: L M X J V S D o M T W T F S S.
                    day.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.getDefault()),
                    style = BwTheme.type.support,
                    color = BwTheme.colors.contentSoft,
                )
                DayDot(result, isToday = day == today)
            }
        }
    }
}

@Composable
private fun DayDot(
    result: DayResult?,
    isToday: Boolean,
) {
    val colors = BwTheme.colors
    val (fill, icon, tint) =
        when (result) {
            DayResult.ALL_TAKEN -> Triple(colors.stateTaken, R.drawable.ic_check, colors.ink)
            DayResult.SOME_MISSED -> Triple(colors.stateMissed, R.drawable.ic_close, colors.ink)
            DayResult.NO_DOSES, null -> Triple(colors.surface, null, colors.content)
        }
    Box(
        modifier =
            Modifier
                .size(Sizes.weekDot)
                .clip(CircleShape)
                .background(fill)
                .then(if (isToday) Modifier.border(Sizes.todayRing, colors.content, CircleShape) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        icon?.let { BwIcon(it, contentDescription = null, color = tint, modifier = Modifier.size(Sizes.weekIcon)) }
    }
}

private const val WEEK = 7

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
