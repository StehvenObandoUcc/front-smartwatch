package com.smartwatch.recordatorios.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Colores del tema: pantalla negra con difuminado púrpura (glow*), acción principal en degradado
 * turquesa-verde, acciones de posponer/omitir en magenta, avisos en amarillo y tiempo en cian.
 * Contrastes (WCAG): texto/fondo ≥ 13:1; contentSoft ≥ 6,7:1; texto sobre principal ≥ 9:1;
 * blanco sobre magenta ≥ 5,3:1. El gris de la referencia no se usa para texto (4,2:1 sobre el glow).
 */
@Immutable
data class BwColors(
    val background: Color,
    val glowCenter: Color,
    val glowMid: Color,
    val surface: Color,
    val outline: Color,
    val content: Color,
    val contentSoft: Color,
    val accent: Color,
    val onAccent: Color,
    val primaryStart: Color,
    val primaryEnd: Color,
    val onPrimary: Color,
    val secondaryStart: Color,
    val secondaryEnd: Color,
    val info: Color,
    val infoDeep: Color,
    val track: Color,
    val success: List<Color>,
)

val AppColors =
    BwColors(
        background = Color(0xFF08080C),
        glowCenter = Color(0xFF3A1F5E),
        glowMid = Color(0xFF1B1233),
        surface = Color(0xFF1E1D2A),
        outline = Color(0xFF3A3850),
        content = Color(0xFFFFFFFF),
        contentSoft = Color(0xFFB7B4CC),
        accent = Color(0xFFF5C518),
        onAccent = Color(0xFF1A1400),
        primaryStart = Color(0xFF3CC4D8),
        primaryEnd = Color(0xFF3EDB86),
        onPrimary = Color(0xFF05140E),
        secondaryStart = Color(0xFFB52DAB),
        secondaryEnd = Color(0xFF8E1F86),
        info = Color(0xFF37C6F4),
        infoDeep = Color(0xFF0A7CFF),
        track = Color(0xFF3A3946),
        success = listOf(Color(0xFFCFFFEA), Color(0xFF6FE9BB), Color(0xFF23B98A), Color(0xFF128A68)),
    )

/** Centro y radio del difuminado púrpura, relativos a la pantalla. */
object Glow {
    const val CENTER_Y = 0.58f
    const val RADIUS = 0.75f
    const val MID_STOP = 0.48f
    const val END_STOP = 0.82f
}

/** Paleta de medicamentos (misma clave que la web), con ≥ 4,3:1 sobre fondo, glow y superficie. */
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
    val outline = 1.dp
    val actionPill = 64.dp
    val mic = 72.dp
    val micRing = 12.dp
    val codeBox = 30.dp
    val icon = 24.dp
    val border = 2.dp
    val touchMin = 48.dp
    val buttonHeight = 52.dp
    val cardRadius = 20.dp
    val progress = 32.dp
    val progressStroke = 4.dp
    val successMark = 64.dp
}
