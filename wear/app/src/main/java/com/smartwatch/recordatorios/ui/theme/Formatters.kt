package com.smartwatch.recordatorios.ui.theme

import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * `epochMillis` es UTC; se muestra en la zona horaria actual del reloj, siempre en español («8:00 p. m.»,
 * como el resto de la app aunque el reloj esté en otro idioma), en 24 h o 12 h según
 * [is24Hour] (el ajuste del sistema, el mismo que usa la hora de arriba de la pantalla).
 */
fun formatTime(
    epochMillis: Long,
    is24Hour: Boolean,
    zone: ZoneId = ZoneId.systemDefault(),
): String {
    val pattern = DateFormat.getBestDateTimePattern(SPANISH, if (is24Hour) "Hm" else "hm")
    return DateTimeFormatter
        .ofPattern(pattern, SPANISH)
        .withZone(zone)
        .format(Instant.ofEpochMilli(epochMillis))
}

/** Una hora del día guardada como «HH:mm» (horarios locales), con el mismo formato que [formatTime]. */
fun formatLocalTime(
    hhmm: String,
    is24Hour: Boolean,
): String {
    val time = runCatching { LocalTime.parse(hhmm) }.getOrNull() ?: return hhmm
    val pattern = DateFormat.getBestDateTimePattern(SPANISH, if (is24Hour) "Hm" else "hm")
    return DateTimeFormatter.ofPattern(pattern, SPANISH).format(time)
}

/** [formatTime] con el ajuste de 12/24 h del reloj. */
@Composable
fun timeText(epochMillis: Long): String = formatTime(epochMillis, DateFormat.is24HourFormat(LocalContext.current))

private val SPANISH: Locale = Locale.forLanguageTag("es")
