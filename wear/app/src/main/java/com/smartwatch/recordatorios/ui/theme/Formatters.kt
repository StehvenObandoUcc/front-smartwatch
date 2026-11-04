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
 * `epochMillis` es UTC; se muestra en la zona horaria actual del reloj, en el idioma de la app («8:00 p. m.»
 * o «8:00 PM»), en 24 h o 12 h según
 * [is24Hour] (el ajuste del sistema, el mismo que usa la hora de arriba de la pantalla).
 */
fun formatTime(
    epochMillis: Long,
    is24Hour: Boolean,
    zone: ZoneId = ZoneId.systemDefault(),
): String {
    val pattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), if (is24Hour) "Hm" else "hm")
    return DateTimeFormatter
        .ofPattern(pattern, Locale.getDefault())
        .withZone(zone)
        .format(Instant.ofEpochMilli(epochMillis))
}

/** Una hora del día guardada como «HH:mm» (horarios locales), con el mismo formato que [formatTime]. */
fun formatLocalTime(
    hhmm: String,
    is24Hour: Boolean,
): String {
    val time = runCatching { LocalTime.parse(hhmm) }.getOrNull() ?: return hhmm
    val pattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), if (is24Hour) "Hm" else "hm")
    return DateTimeFormatter.ofPattern(pattern, Locale.getDefault()).format(time)
}

/** [formatTime] con el ajuste de 12/24 h del reloj. */
@Composable
fun timeText(epochMillis: Long): String = formatTime(epochMillis, DateFormat.is24HourFormat(LocalContext.current))
