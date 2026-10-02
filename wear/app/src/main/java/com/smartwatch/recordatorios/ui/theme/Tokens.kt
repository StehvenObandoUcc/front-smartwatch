package com.smartwatch.recordatorios.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** Paleta de medicamentos (misma clave que la web). El color siempre acompaña al nombre. */
private val medicationPalette =
    mapOf(
        "red" to Color(0xFFEF5350),
        "orange" to Color(0xFFFF9A52),
        "yellow" to Color(0xFFFFD54F),
        "green" to Color(0xFF66BB6A),
        "teal" to Color(0xFF4DB6AC),
        "blue" to Color(0xFF64B5F6),
        "purple" to Color(0xFFBA68C8),
        "pink" to Color(0xFFF06292),
    )

fun medicationColor(colorKey: String): Color =
    medicationPalette[colorKey]
        ?: runCatching { Color(android.graphics.Color.parseColor(colorKey)) }.getOrNull()
        ?: medicationPalette.getValue("blue")

object Spacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
}

object Sizes {
    val colorDot = 14.dp
}
