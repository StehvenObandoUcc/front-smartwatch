package com.smartwatch.recordatorios.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Colores de Recuerda en el reloj: la misma señalética de salud que el panel web sobre un fondo
 * casi negro con tinte petróleo (pantalla OLED). Papeles: la acción principal es una placa petróleo
 * plana con texto blanco (una por pantalla), los avisos en el amarillo de sol, cada estado de una
 * dosis con su placa (Tomada, Pendiente, No tomada, Omitida) y las franjas del día como placas de
 * color (sol, tarde, noche). Lo demás, neutro con tinte petróleo. El celeste petróleo es para datos.
 * Contrastes (WCAG): blanco sobre fondo 19,8:1; contentSoft ≥ 9,6:1; blanco sobre principal 5,2:1 y
 * la placa principal sobre el fondo 3,8:1; blanco sobre cada estado ≥ 5,1:1; tinta sobre sol 10:1 y
 * sobre tarde 6,8:1; aviso sobre superficie 9,7:1; datos sobre fondo 9,4:1.
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
    val info: Color,
    val infoDeep: Color,
    val track: Color,
    val success: List<Color>,
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
        background = Color(0xFF050B0A),
        glowCenter = Color(0xFF0B2422),
        glowMid = Color(0xFF071513),
        surface = Color(0xFF142320),
        outline = Color(0xFF3A5A56),
        content = Color(0xFFFFFFFF),
        contentSoft = Color(0xFFB5CCC9),
        accent = Color(0xFFF2C14E),
        onAccent = Color(0xFF11201F),
        // Placa plana, como una señal: el inicio y el final del degradado son el mismo petróleo.
        primaryStart = Color(0xFF127A73),
        primaryEnd = Color(0xFF127A73),
        onPrimary = Color(0xFFFFFFFF),
        info = Color(0xFF5FC2BA),
        infoDeep = Color(0xFF127A73),
        track = Color(0xFF23332F),
        success = listOf(Color(0xFF4CAF50), Color(0xFF3E9A42), Color(0xFF2F7D32), Color(0xFF24622A)),
        stateTaken = Color(0xFF2F7D32),
        stateDue = Color(0xFF9A5B00),
        stateMissed = Color(0xFFB3261E),
        stateSkipped = Color(0xFF5B6B69),
        sun = Color(0xFFF2C14E),
        dusk = Color(0xFFF08A4B),
        night = Color(0xFF2B3A67),
        ink = Color(0xFF11201F),
    )

/** Centro y radio del difuminado petróleo (muy tenue), relativos a la pantalla. */
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

/** Escala tipográfica: nada por debajo de 14 sp; el dato que se mira de un vistazo a 40 sp. */
object TypeScale {
    val hero = 40.sp
    val title = 18.sp
    val body = 16.sp
    val support = 14.sp
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
    val progress = 32.dp
    val progressStroke = 4.dp
    val successMark = 64.dp
    val badgeIcon = 32.dp
    val barChart = 48.dp
    val bar = 5.dp
}
