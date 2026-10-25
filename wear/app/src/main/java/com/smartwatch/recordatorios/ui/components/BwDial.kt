package com.smartwatch.recordatorios.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.smartwatch.recordatorios.ui.theme.AtkinsonNext
import com.smartwatch.recordatorios.ui.theme.BwTheme
import kotlin.math.cos
import kotlin.math.sin

/**
 * La esfera de 24 horas, firma de Recuerda en el reloj: la escala en el borde (cuartos de hora,
 * horas marcadas y números pares, medianoche arriba y mediodía abajo), el [arc] resaltado (de ahora a
 * la próxima toma, o el día completo), la marca de [now] y cada dosis como un punto a su hora.
 * Decorativa para TalkBack: la pantalla dice lo mismo en palabras. Los números 20 y 04 se omiten
 * porque quedan bajo los botones de las esquinas.
 */
@Composable
fun BwDial(
    modifier: Modifier = Modifier,
    marks: List<DialMark> = emptyList(),
    arc: ClosedFloatingPointRange<Float>? = null,
    now: Float? = null,
    numbers: Boolean = true,
) {
    val colors = BwTheme.colors
    val measurer = rememberTextMeasurer()
    val numberStyle = TextStyle(fontFamily = AtkinsonNext, fontSize = NUMBER_SP.sp, color = colors.contentSoft)
    Canvas(modifier.fillMaxSize().clearAndSetSemantics {}) {
        val r = size.minDimension / 2
        val c = Offset(size.width / 2, size.height / 2)

        fun at(
            hour: Float,
            radius: Float,
        ): Offset {
            val a = hour / HOURS * FULL_TURN_RAD - QUARTER_TURN_RAD
            return Offset(c.x + radius * cos(a), c.y + radius * sin(a))
        }
        val tickOuter = r * TICK_OUTER
        for (q in 0 until QUARTERS) {
            val major = q % QUARTERS_PER_HOUR == 0
            val h = q / QUARTERS_PER_HOUR.toFloat()
            drawLine(
                colors.outline,
                at(h, tickOuter),
                at(h, r * if (major) TICK_MAJOR else TICK_MINOR),
                strokeWidth = if (major) MAJOR_STROKE else MINOR_STROKE,
            )
        }
        if (numbers) {
            for (h in EVEN_HOURS) {
                val layout = measurer.measure(h.toString().padStart(2, '0'), numberStyle)
                val p = at(h.toFloat(), r * NUMBER_RADIUS)
                drawText(layout, topLeft = Offset(p.x - layout.size.width / 2f, p.y - layout.size.height / 2f))
            }
        }
        val ring = r * RING_RADIUS
        arc?.let { drawArcHours(it, ring, colors.info) }
        now?.let { drawCircle(colors.info, radius = r * NOW_DOT, center = at(it, ring)) }
        marks.forEach { m ->
            val p = at(m.hour, ring)
            if (m.filled) {
                drawCircle(m.color, radius = r * MARK_DOT, center = p)
            } else {
                drawCircle(m.color, radius = r * MARK_DOT, center = p, style = Stroke(width = r * MARK_STROKE))
            }
        }
    }
}

private fun DrawScope.drawArcHours(
    range: ClosedFloatingPointRange<Float>,
    radius: Float,
    color: Color,
) {
    val start = range.start / HOURS * FULL_TURN_DEG - QUARTER_TURN_DEG
    val sweep = ((range.endInclusive - range.start + HOURS) % HOURS) / HOURS * FULL_TURN_DEG
    val topLeft = Offset(center.x - radius, center.y - radius)
    drawArc(
        color,
        startAngle = start,
        sweepAngle = sweep,
        useCenter = false,
        topLeft = topLeft,
        size = Size(radius * 2, radius * 2),
        style = Stroke(width = size.minDimension * ARC_WIDTH, cap = StrokeCap.Round),
    )
}

private const val HOURS = 24f
private const val QUARTERS = 96
private const val QUARTERS_PER_HOUR = 4
private const val FULL_TURN_DEG = 360f
private const val QUARTER_TURN_DEG = 90f
private const val FULL_TURN_RAD = (2 * Math.PI).toFloat()
private const val QUARTER_TURN_RAD = (Math.PI / 2).toFloat()
private const val TICK_OUTER = 0.985f
private const val TICK_MAJOR = 0.9f
private const val TICK_MINOR = 0.95f
private const val MAJOR_STROKE = 2.4f
private const val MINOR_STROKE = 1.2f
private const val NUMBER_RADIUS = 0.79f
private const val NUMBER_SP = 14
private const val RING_RADIUS = 0.845f
private const val NOW_DOT = 0.035f
private const val MARK_DOT = 0.05f
private const val MARK_STROKE = 0.022f
private const val ARC_WIDTH = 0.018f

// Horas pares de 02 a 22; 04 y 20 quedan bajo los botones de las esquinas.
private const val FIRST_NUMBER = 2
private const val LAST_NUMBER = 22
private const val UNDER_START_CORNER = 20
private const val UNDER_END_CORNER = 4
private val UNDER_CORNERS = setOf(UNDER_START_CORNER, UNDER_END_CORNER)
private val EVEN_HOURS = (FIRST_NUMBER..LAST_NUMBER step 2).filterNot { it in UNDER_CORNERS }
