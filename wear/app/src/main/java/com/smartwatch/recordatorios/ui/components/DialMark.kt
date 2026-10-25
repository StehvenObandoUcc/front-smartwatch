package com.smartwatch.recordatorios.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.smartwatch.recordatorios.data.local.DoseEntity
import com.smartwatch.recordatorios.data.local.DoseStatus
import com.smartwatch.recordatorios.ui.theme.BwTheme
import java.time.Instant
import java.time.ZoneId

/** Una dosis en la esfera: su hora del día (0-24) y cómo se dibuja. */
data class DialMark(
    val hour: Float,
    val color: Color,
    val filled: Boolean,
)

/** Hora local decimal (0-24) de un instante UTC, en la zona del reloj. */
fun hourOfDay(
    epochMillis: Long,
    zone: ZoneId = ZoneId.systemDefault(),
): Float {
    val t = Instant.ofEpochMilli(epochMillis).atZone(zone).toLocalTime()
    return t.hour + t.minute / MINUTES_PER_HOUR
}

/**
 * Las dosis del día como puntos de la esfera: verde llena si se tomó, gris llena si se omitió,
 * naranja llena si ya es su hora y falta registrarla, y aguamarina hueca si aún no toca.
 */
@Composable
fun dialMarks(
    doses: List<DoseEntity>,
    now: Long,
): List<DialMark> {
    val colors = BwTheme.colors
    return doses.map { dose ->
        val hour = hourOfDay(dose.scheduledAt)
        when {
            dose.status == DoseStatus.TAKEN -> DialMark(hour, colors.stateTaken, filled = true)
            dose.status == DoseStatus.SKIPPED -> DialMark(hour, colors.stateSkipped, filled = true)
            dose.scheduledAt <= now -> DialMark(hour, colors.stateDue, filled = true)
            else -> DialMark(hour, colors.info, filled = false)
        }
    }
}

private const val MINUTES_PER_HOUR = 60f
