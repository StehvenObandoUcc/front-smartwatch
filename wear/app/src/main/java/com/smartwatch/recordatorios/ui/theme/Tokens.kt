package com.smartwatch.recordatorios.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Colores del tema. Sin negro ni blanco puros; [content] es a la vez texto y borde, y las capas
 * se separan con borde de 2 dp, no solo con tono. Contrastes (WCAG): texto/fondo ≥ 11:1.
 */
@Immutable
data class BwColors(
    val background: Color,
    val surface: Color,
    val content: Color,
    val accent: Color,
)

val DarkColors =
    BwColors(
        background = Color(0xFF2B3038),
        surface = Color(0xFF373D47),
        content = Color(0xFFEDEFF2),
        accent = Color(0xFFFFC94D),
    )

/** Paleta de medicamentos (misma clave que la web), con ≥ 3:1 sobre fondo y superficie. */
private val medicationPalette =
    mapOf(
        "red" to Color(0xFFF0625F),
        "orange" to Color(0xFFFF8A3D),
        "yellow" to Color(0xFFE6E65C),
        "green" to Color(0xFF6CC070),
        "teal" to Color(0xFF4DC4C4),
        "blue" to Color(0xFF6AA8F5),
        "purple" to Color(0xFFB58CF0),
        "pink" to Color(0xFFF277B8),
    )

/** El color siempre acompaña al nombre. */
fun medicationColor(colorKey: String): Color =
    medicationPalette[colorKey]
        ?: runCatching { Color(android.graphics.Color.parseColor(colorKey)) }.getOrNull()
        ?: medicationPalette.getValue("blue")

/** Escala tipográfica: nada por debajo de 14 sp; datos clave a 16 sp o más. */
object TypeScale {
    val data = 26.sp
    val title = 18.sp
    val body = 16.sp
    val support = 14.sp
    val code = 32.sp
}

object Spacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
}

object Sizes {
    val colorDot = 14.dp
    val icon = 24.dp
    val border = 2.dp
    val touchMin = 48.dp
    val buttonHeight = 52.dp
    val cardRadius = 20.dp
    val progress = 32.dp
    val progressStroke = 4.dp
    val successMark = 56.dp
}
