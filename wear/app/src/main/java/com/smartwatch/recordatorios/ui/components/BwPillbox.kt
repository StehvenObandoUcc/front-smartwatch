package com.smartwatch.recordatorios.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.Dp
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.Sizes

/**
 * El día como un pastillero: una casilla por dosis. Para TalkBack es una sola frase ([description]),
 * p. ej. «2 de 5 tomadas hoy». Con muchas dosis pasa a dos filas en vez de salirse del círculo.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BwPillbox(
    states: List<PillState>,
    description: String,
    modifier: Modifier = Modifier,
    cell: Dp = Sizes.pillCell,
) {
    FlowRow(
        modifier = modifier.clearAndSetSemantics { contentDescription = description },
        horizontalArrangement = Arrangement.spacedBy(Sizes.pillGap, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(Sizes.pillGap),
        maxItemsInEachRow = MAX_PER_ROW,
    ) {
        states.forEach { PillCell(it, cell) }
    }
}

@Composable
private fun PillCell(
    state: PillState,
    cell: Dp,
) {
    val colors = BwTheme.colors
    Canvas(Modifier.size(cell)) {
        val radius = CornerRadius(Sizes.pillCellRadius.toPx() * size.width / Sizes.pillCell.toPx())
        drawRoundRect(if (state == PillState.DONE) colors.stateTaken else colors.surface, cornerRadius = radius)
        when (state) {
            PillState.DONE -> {
                val check =
                    Path().apply {
                        moveTo(size.width * CHECK_X0, size.height * CHECK_Y0)
                        lineTo(size.width * CHECK_X1, size.height * CHECK_Y1)
                        lineTo(size.width * CHECK_X2, size.height * CHECK_Y2)
                    }
                drawPath(
                    check,
                    colors.ink,
                    style = Stroke(size.width * MARK_W, cap = StrokeCap.Round, join = StrokeJoin.Round),
                )
            }
            PillState.NEXT -> {
                val stroke = Sizes.pillNextStroke.toPx()
                drawRoundRect(
                    colors.info,
                    topLeft = Offset(stroke / 2, stroke / 2),
                    size = Size(size.width - stroke, size.height - stroke),
                    cornerRadius = radius,
                    style = Stroke(stroke),
                )
            }
            PillState.SKIPPED ->
                drawLine(
                    colors.stateMissed,
                    Offset(size.width * SLASH_IN, size.height * (1 - SLASH_IN)),
                    Offset(size.width * (1 - SLASH_IN), size.height * SLASH_IN),
                    size.width * MARK_W,
                    cap = StrokeCap.Round,
                )
            PillState.LATER -> Unit
        }
    }
}

private const val MAX_PER_ROW = 6
private const val MARK_W = 0.11f
private const val SLASH_IN = 0.26f
private const val CHECK_X0 = 0.27f
private const val CHECK_Y0 = 0.52f
private const val CHECK_X1 = 0.43f
private const val CHECK_Y1 = 0.68f
private const val CHECK_X2 = 0.74f
private const val CHECK_Y2 = 0.34f
