package com.smartwatch.recordatorios.ui.theme

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/** `epochMillis` es UTC; se muestra en la zona horaria actual del reloj. */
fun formatTime(
    epochMillis: Long,
    zone: ZoneId = ZoneId.systemDefault(),
): String =
    DateTimeFormatter
        .ofLocalizedTime(FormatStyle.SHORT)
        .withZone(zone)
        .format(Instant.ofEpochMilli(epochMillis))
