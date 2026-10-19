package com.smartwatch.recordatorios.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import com.smartwatch.recordatorios.R
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.Sizes
import com.smartwatch.recordatorios.ui.theme.Spacing

private const val SPIN_MILLIS = 1000
private const val FULL_TURN = 360f
private const val ARC_SWEEP = 270f

/** Indicador de carga: arco cian que gira. TalkBack lo anuncia como «Cargando». */
@Composable
fun BwProgress(modifier: Modifier = Modifier) {
    val color = BwTheme.colors.info
    val description = stringResource(R.string.loading)
    val angle by rememberInfiniteTransition(label = "progress").animateFloat(
        initialValue = 0f,
        targetValue = FULL_TURN,
        animationSpec = infiniteRepeatable(tween(SPIN_MILLIS, easing = LinearEasing), RepeatMode.Restart),
        label = "angle",
    )
    Canvas(
        modifier =
            modifier.size(Sizes.progress).semantics {
                contentDescription = description
                progressBarRangeInfo = ProgressBarRangeInfo.Indeterminate
            },
    ) {
        drawArc(
            color = color,
            startAngle = angle,
            sweepAngle = ARC_SWEEP,
            useCenter = false,
            style = Stroke(width = Sizes.progressStroke.toPx(), cap = StrokeCap.Round),
        )
    }
}

/** Marca de éxito: insignia verde con brillo y visto blanco dibujado (sin depender de la fuente), y su texto. */
@Composable
fun BwSuccessMark(
    label: String,
    modifier: Modifier = Modifier,
) {
    val colors = BwTheme.colors
    Column(
        modifier = modifier.semantics(mergeDescendants = true) {},
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Canvas(modifier = Modifier.size(Sizes.successMark).clearAndSetSemantics {}) {
            drawCircle(
                Brush.radialGradient(
                    colors = colors.success,
                    center = Offset(size.width * BADGE_LIGHT_X, size.height * BADGE_LIGHT_Y),
                    radius = size.minDimension,
                ),
            )
            val check =
                Path().apply {
                    moveTo(size.width * CHECK_START_X, size.height * CHECK_MID_Y)
                    lineTo(size.width * CHECK_MID_X, size.height * CHECK_END_Y)
                    lineTo(size.width * CHECK_END_X, size.height * CHECK_START_Y)
                }
            drawPath(
                check,
                colors.content,
                style = Stroke(width = Sizes.progressStroke.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
            )
        }
        BwText(label, style = BwTheme.type.display, textAlign = TextAlign.Center)
    }
}

private const val BADGE_LIGHT_X = 0.35f
private const val BADGE_LIGHT_Y = 0.28f
private const val CHECK_START_X = 0.28f
private const val CHECK_MID_X = 0.44f
private const val CHECK_END_X = 0.74f
private const val CHECK_START_Y = 0.36f
private const val CHECK_MID_Y = 0.52f
private const val CHECK_END_Y = 0.68f
