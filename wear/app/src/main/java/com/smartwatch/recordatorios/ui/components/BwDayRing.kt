package com.smartwatch.recordatorios.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.Sizes

/**
 * Anillo del día por el borde de la esfera, como el del panel web: un tramo por dosis de hoy,
 * relleno en verde si se tomó, con un hueco arriba para la hora. TalkBack lo lee como
 * [description] («1 de 3 tomadas hoy»).
 */
@Composable
fun BwDayRing(
    taken: Int,
    total: Int,
    description: String,
    modifier: Modifier = Modifier,
) {
    if (total == 0) return
    val colors = BwTheme.colors
    Canvas(
        modifier
            .fillMaxSize()
            .padding(Sizes.ringInset)
            .clearAndSetSemantics { contentDescription = description },
    ) {
        val stroke = Sizes.ringStroke.toPx()
        val gap = if (total > 1) GAP_DEGREES else 0f
        // Arriba queda libre el hueco de la hora curvada del sistema.
        val sweep = (ARC - gap * total) / total
        val inset = stroke / 2
        val arcSize = Size(size.width - stroke, size.height - stroke)
        // Con muchos tramos, los extremos redondos se comerían la separación: van rectos.
        val cap = if (total > MAX_ROUND_SEGMENTS) StrokeCap.Butt else StrokeCap.Round
        repeat(total) { i ->
            drawArc(
                color = if (i < taken) colors.stateTaken else colors.track,
                startAngle = START + i * (sweep + gap) + gap / 2,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = stroke, cap = cap),
            )
        }
    }
}

private const val TIME_GAP = 60f
private const val ARC = 360f - TIME_GAP

// Empieza a la derecha del hueco de la hora (las 12 son -90°) y gira en el sentido del reloj.
private const val START = -90f + TIME_GAP / 2
private const val GAP_DEGREES = 6f
private const val MAX_ROUND_SEGMENTS = 12
