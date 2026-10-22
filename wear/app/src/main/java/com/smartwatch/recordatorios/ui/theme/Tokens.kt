package com.smartwatch.recordatorios.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Colores de Recuerda en el reloj: la misma señalética de salud que el panel web sobre un fondo
 * negro plano (pantalla OLED, sin difuminados). Papeles: la acción principal es una placa petróleo
 * plana con texto blanco (una por pantalla), los avisos en el amarillo de sol, cada estado de una
 * dosis con su placa (Tomada, Pendiente, No tomada, Omitida) y las franjas del día como placas de
 * color (sol, tarde, noche). Lo demás, neutro con tinte petróleo. El celeste petróleo es para datos.
 * Contrastes (WCAG): blanco sobre fondo 21:1; contentSoft ≥ 9,6:1; blanco sobre principal 5,2:1 y
 * la placa principal sobre el fondo 3,8:1; blanco sobre cada estado ≥ 5,1:1; tinta sobre sol 10:1 y
 * sobre tarde 6,8:1; aviso sobre superficie 9,7:1; datos sobre fondo 9,4:1.
 */
@Immutable
data class BwColors(
    val background: Color,
    val surface: Color,
    val outline: Color,
    val content: Color,
    val contentSoft: Color,
    val accent: Color,
    val onAccent: Color,
    val primary: Color,
    val onPrimary: Color,
    val info: Color,
    val track: Color,
    val stateTaken: Color,
    val stateDue: Color,
    val stateMissed: Color,
    val stateSkipped: Color,
    val sun: Color,
    val dusk: Color,
    val night: Color,
    /** Tinta para texto e iconos sobre las placas claras (sol, tarde). */
    val ink: Color,
)

val AppColors =
    BwColors(
        background = Color(0xFF000000),
        surface = Color(0xFF142320),
        outline = Color(0xFF3A5A56),
        content = Color(0xFFFFFFFF),
        contentSoft = Color(0xFFB5CCC9),
        accent = Color(0xFFF2C14E),
        onAccent = Color(0xFF11201F),
        // Placa plana, como una señal.
        primary = Color(0xFF127A73),
        onPrimary = Color(0xFFFFFFFF),
        info = Color(0xFF5FC2BA),
        track = Color(0xFF23332F),
        stateTaken = Color(0xFF2F7D32),
        stateDue = Color(0xFF9A5B00),
        stateMissed = Color(0xFFB3261E),
        stateSkipped = Color(0xFF5B6B69),
        sun = Color(0xFFF2C14E),
        dusk = Color(0xFFF08A4B),
        night = Color(0xFF2B3A67),
        ink = Color(0xFF11201F),
    )

/**
 * Paleta de medicamentos: las mismas claves y los mismos tonos profundos que el panel web (tokens
 * `--med-*`), con el pictograma blanco legible encima (≥ 3:1).
 */
private val medicationPalette =
    mapOf(
        "red" to Color(0xFFC62828),
        "orange" to Color(0xFFD4661A),
        "yellow" to Color(0xFFB08900),
        "green" to Color(0xFF2E7D32),
        "teal" to Color(0xFF00796B),
        "blue" to Color(0xFF1565C0),
        "purple" to Color(0xFF6A1B9A),
        "pink" to Color(0xFFAD1457),
    )

/**
 * El color siempre acompaña al nombre. Un hex de la API (a veces un 500 de Tailwind) se lleva al tono
 * de la paleta más cercano, como en la web.
 */
fun medicationColor(colorKey: String): Color {
    medicationPalette[colorKey]?.let { return it }
    val hex = runCatching { Color(android.graphics.Color.parseColor(colorKey)) }.getOrNull()
    return hex?.let(::nearestMedicationColor) ?: medicationPalette.getValue("teal")
}

/** El tono de la paleta más cercano (por matiz, no por distancia RGB: un verde vivo no es turquesa). */
internal fun nearestMedicationColor(color: Color): Color {
    val h = color.hue()
    return medicationPalette.values.minBy { c ->
        val diff = kotlin.math.abs(c.hue() - h)
        minOf(diff, FULL_CIRCLE - diff)
    }
}

/** Matiz en grados (0-360). */
private fun Color.hue(): Float {
    val max = maxOf(red, green, blue)
    val d = max - minOf(red, green, blue)
    if (d == 0f) return 0f
    val sector =
        when (max) {
            red -> ((green - blue) / d).mod(SECTORS)
            green -> (blue - red) / d + 2
            else -> (red - green) / d + BLUE_SECTOR
        }
    return sector * DEGREES_PER_SECTOR
}

private const val SECTORS = 6f
private const val BLUE_SECTOR = 4f
private const val DEGREES_PER_SECTOR = 60f
private const val FULL_CIRCLE = 360f

/** Escala tipográfica: nada por debajo de 14 sp; el dato que se mira de un vistazo a 40 sp. */
object TypeScale {
    val hero = 40.sp
    val title = 18.sp
    val body = 16.sp
    val support = 14.sp

    /** Solo para la dirección del panel, que debe caber entera en una línea. */
    val url = 12.sp
    val code = 32.sp
}

/** A partir de esta escala de fuente del sistema, las filas pasan a apilarse (icono u hora encima). */
const val LARGE_FONT_SCALE = 1.15f

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
    val touchMin = 48.dp
    val buttonHeight = 52.dp
    val cardRadius = 12.dp

    /** Placa de pictograma: la del encabezado y la del medicamento. */
    val plate = 28.dp
    val plateIcon = 18.dp
    val plateSmall = 22.dp
    val plateSmallIcon = 14.dp
    val plateRadius = 6.dp

    /** Alto de la franja del día (señal, no botón). */
    val signBar = 32.dp
    val progress = 32.dp
    val progressStroke = 4.dp
    val successMark = 64.dp
    val badgeIcon = 32.dp
    val barChart = 48.dp
    val bar = 5.dp
}
