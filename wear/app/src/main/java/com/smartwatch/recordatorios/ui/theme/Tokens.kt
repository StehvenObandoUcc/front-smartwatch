package com.smartwatch.recordatorios.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Colores de un modo. Sin negro ni blanco puros; [content] es a la vez texto y borde, y las capas
 * se separan con borde de 2 dp, no solo con tono. Contrastes (WCAG): texto/fondo ≥ 11:1 en ambos modos.
 */
@Immutable
data class BwColors(
    val background: Color,
    val surface: Color,
    val content: Color,
    val accent: Color,
    val isDark: Boolean,
)

val DarkColors =
    BwColors(
        background = Color(0xFF2B3038),
        surface = Color(0xFF373D47),
        content = Color(0xFFEDEFF2),
        accent = Color(0xFFFFC94D),
        isDark = true,
    )

val LightColors =
    BwColors(
        background = Color(0xFFDCDFE4),
        surface = Color(0xFFECEEF1),
        content = Color(0xFF23272E),
        accent = Color(0xFF7A4E00),
        isDark = false,
    )

/** Paleta de medicamentos (misma clave que la web), una versión por modo con ≥ 3:1 sobre fondo y superficie. */
private val medicationPaletteDark =
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

private val medicationPaletteLight =
    mapOf(
        "red" to Color(0xFFC62828),
        "orange" to Color(0xFFA84A00),
        "yellow" to Color(0xFF6B6B00),
        "green" to Color(0xFF2E7D32),
        "teal" to Color(0xFF00796B),
        "blue" to Color(0xFF1565C0),
        "purple" to Color(0xFF7B3FB5),
        "pink" to Color(0xFFB0286E),
    )

/** El color siempre acompaña al nombre. Por defecto, la versión oscura (la notificación usa esa). */
fun medicationColor(
    colorKey: String,
    dark: Boolean = true,
): Color {
    val palette = if (dark) medicationPaletteDark else medicationPaletteLight
    return palette[colorKey]
        ?: runCatching { Color(android.graphics.Color.parseColor(colorKey)) }.getOrNull()
        ?: palette.getValue("blue")
}

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
    val border = 2.dp
    val touchMin = 48.dp
    val buttonHeight = 52.dp
    val cardRadius = 20.dp
    val progress = 32.dp
    val progressStroke = 4.dp
    val successMark = 56.dp
}
