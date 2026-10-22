package com.smartwatch.recordatorios.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

/** Indicador de carga: arco celeste que gira. TalkBack lo anuncia como «Cargando». */
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

/**
 * Marca de éxito: disco plano del verde de «Tomada» con el visto blanco dibujado (sin depender de la
 * fuente), su texto y [details] debajo (p. ej. qué se registró y a qué hora).
 */
@Composable
fun BwSuccessMark(
    label: String,
    modifier: Modifier = Modifier,
    details: List<String> = emptyList(),
) {
    val colors = BwTheme.colors
    StatusMark(label, details, modifier) {
        Canvas(modifier = Modifier.size(Sizes.successMark)) {
            drawCircle(colors.stateTaken)
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
    }
}

/** Marca de pospuesta: la misma composición que [BwSuccessMark], con una insignia neutra y el reloj. */
@Composable
fun BwSnoozeMark(
    label: String,
    modifier: Modifier = Modifier,
    details: List<String> = emptyList(),
) {
    val colors = BwTheme.colors
    StatusMark(label, details, modifier) {
        Box(
            modifier =
                Modifier
                    .size(Sizes.successMark)
                    .clip(CircleShape)
                    .background(colors.surface)
                    .border(Sizes.outline, colors.outline, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            BwIcon(
                R.drawable.ic_snooze,
                contentDescription = null,
                color = colors.content,
                modifier = Modifier.size(Sizes.badgeIcon),
            )
        }
    }
}

/** Insignia arriba, [label] grande (encoge si no cabe) y [details] en tono suave; TalkBack lo lee junto. */
@Composable
private fun StatusMark(
    label: String,
    details: List<String>,
    modifier: Modifier,
    badge: @Composable () -> Unit,
) {
    Column(
        modifier = modifier.semantics(mergeDescendants = true) {},
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Box(Modifier.clearAndSetSemantics {}) { badge() }
        BwText(
            label,
            style = BwTheme.type.display,
            textAlign = TextAlign.Center,
            maxLines = 1,
            autoSize = BwTheme.type.displayFit,
            modifier = Modifier.fillMaxWidth(),
        )
        details.forEach { BwText(it, color = BwTheme.colors.contentSoft, textAlign = TextAlign.Center) }
    }
}

/**
 * Estado vacío: icono, qué pasa y el siguiente paso, centrado. Nunca un texto suelto sin salida.
 */
@Composable
fun BwEmptyState(
    @DrawableRes icon: Int,
    title: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(vertical = Spacing.lg).semantics(mergeDescendants = true) {},
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        BwIcon(
            icon,
            contentDescription = null,
            color = BwTheme.colors.contentSoft,
            modifier = Modifier.size(Sizes.badgeIcon),
        )
        BwText(title, style = BwTheme.type.title, textAlign = TextAlign.Center)
        BwText(body, color = BwTheme.colors.contentSoft, textAlign = TextAlign.Center)
    }
}

private const val CHECK_START_X = 0.28f
private const val CHECK_MID_X = 0.44f
private const val CHECK_END_X = 0.74f
private const val CHECK_START_Y = 0.36f
private const val CHECK_MID_Y = 0.52f
private const val CHECK_END_Y = 0.68f
