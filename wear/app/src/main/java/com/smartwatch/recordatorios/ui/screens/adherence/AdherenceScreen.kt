package com.smartwatch.recordatorios.ui.screens.adherence

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.smartwatch.recordatorios.R
import com.smartwatch.recordatorios.data.remote.AdherenceDay
import com.smartwatch.recordatorios.data.remote.AdherenceSummary
import com.smartwatch.recordatorios.ui.components.BwEmptyState
import com.smartwatch.recordatorios.ui.components.BwHeader
import com.smartwatch.recordatorios.ui.components.BwScreenScaffold
import com.smartwatch.recordatorios.ui.components.BwText
import com.smartwatch.recordatorios.ui.components.edgeItem
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.RecordatoriosTheme
import com.smartwatch.recordatorios.ui.theme.Sizes
import com.smartwatch.recordatorios.ui.theme.Spacing
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Adherencia de los últimos días: porcentaje grande y una barra por día (alto = tomadas / vencidas).
 * Las barras son decorativas para TalkBack; el texto ya dice el porcentaje y el periodo.
 */
@Composable
fun AdherenceScreen(summary: AdherenceSummary?) {
    val listState = rememberTransformingLazyColumnState()
    BwScreenScaffold(scrollState = listState) { contentPadding ->
        TransformingLazyColumn(state = listState, contentPadding = contentPadding) {
            edgeItem { BwHeader(stringResource(R.string.adherence_title), icon = R.drawable.ic_chart) }
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
                edgeItem {
                    CenteredText(
                        text = summary.percentage?.let { stringResource(R.string.adherence_value, it) } ?: "—",
                        large = true,
                    )
                }
                edgeItem { DayBars(summary.days, modifier = ChartInset.fillMaxWidth().height(Sizes.barChart)) }
                edgeItem {
                    Row(modifier = ChartInset.fillMaxWidth()) {
                        BwText(
                            summary.from.format(DAY_FORMAT),
                            style = BwTheme.type.support,
                            modifier = Modifier.weight(1f),
                        )
                        BwText(stringResource(R.string.adherence_today), style = BwTheme.type.support)
                    }
                }
                edgeItem {
                    CenteredText(
                        stringResource(
                            if (summary.percentage == null) R.string.adherence_none else R.string.adherence_caption,
                            summary.days.size,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun DayBars(
    days: List<AdherenceDay>,
    modifier: Modifier = Modifier,
) {
    val filled = BwTheme.colors.info
    val empty = BwTheme.colors.track
    Canvas(modifier = modifier.clearAndSetSemantics {}) {
        if (days.isEmpty()) return@Canvas
        val barWidth = Sizes.bar.toPx()
        val step = (size.width - barWidth) / (days.size - 1).coerceAtLeast(1)
        val radius = CornerRadius(barWidth / 2)
        days.forEachIndexed { index, day ->
            val x = index * step
            // Un día sin dosis vencidas lleva solo una marca baja, para que no parezca un 0 %.
            val ratio = if (day.total == 0) 0f else day.taken.toFloat() / day.total
            val height = (size.height * ratio).coerceAtLeast(barWidth)
            drawRoundRect(
                color = if (day.total == 0) empty else filled,
                topLeft = Offset(x, size.height - height),
                size = Size(barWidth, height),
                cornerRadius = radius,
            )
        }
    }
}

@Composable
private fun CenteredText(
    text: String,
    large: Boolean = false,
) {
    BwText(
        text = text,
        autoSize = if (large) BwTheme.type.displayFit else null,
        maxLines = if (large) 1 else Int.MAX_VALUE,
        style = if (large) BwTheme.type.display else BwTheme.type.body,
        color = if (large) BwTheme.colors.content else BwTheme.colors.contentSoft,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
}

/** La gráfica va en la parte baja, donde la pantalla redonda es más estrecha. */
private val ChartInset = Modifier.padding(horizontal = Spacing.lg + Spacing.md)

private val DAY_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM")

@Suppress("MagicNumber")
@WearPreviewDevices
@Composable
private fun AdherenceScreenPreview() {
    RecordatoriosTheme {
        AdherenceScreen(
            AdherenceSummary(
                percentage = 86,
                from = LocalDate.of(2026, 10, 6),
                days = List(14) { AdherenceDay(taken = it % 3, total = 2) },
            ),
        )
    }
}
