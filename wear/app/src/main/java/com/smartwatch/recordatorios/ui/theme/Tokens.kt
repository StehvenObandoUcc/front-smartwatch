package com.smartwatch.recordatorios.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Colores de Recuerda en el reloj: esferas a sangre con cuatro colores como máximo.
 * - Petróleo (principal): el fondo de casi todas las pantallas, en degradado tonal [BwTones].
 * - Aguamarina (su complemento claro): arcos, «ahora» y lo pendiente ([info]).
 * - Naranja rojizo: la alarma ([BwTones.alert]) y lo que pide atención ([stateDue], [stateMissed]).
 * - Verde: Tomada ([BwTones.done], [stateTaken]).
 * Lo demás es neutro: el claro cálido de texto, la tinta para lo que va sobre rellenos claros y el
 * vidrio (claro translúcido) de superficies y botones secundarios. Ni negro ni blanco puros.
 * La acción principal es un relleno claro con tinta encima: contrasta sobre cualquier fondo.
 * Contrastes (WCAG): claro sobre el extremo claro de cada degradado ≥ 4,8:1; tinta sobre la acción
 * principal 16:1, sobre aguamarina 11,5:1, sobre verde claro 11,8:1, sobre naranja claro 10,1:1 y
 * sobre rojo claro 7,8:1.
 */
@Immutable
data class BwColors(
    val background: Color,
    /** Vidrio: superficies, tarjetas y botones secundarios sobre la esfera. */
    val surface: Color,
    val outline: Color,
    val content: Color,
    val contentSoft: Color,
    /** Aviso que pide atención (permisos, sin conexión): naranja claro con tinta. */
    val accent: Color,
    val onAccent: Color,
    /** Acción principal: relleno claro con tinta encima. */
    val primary: Color,
    val onPrimary: Color,
    /** Aguamarina: arcos, «ahora», datos y lo pendiente. */
    val info: Color,
    val track: Color,
    val stateTaken: Color,
    val stateDue: Color,
    val stateMissed: Color,
    val stateSkipped: Color,
    /** Tinta para texto e iconos sobre los rellenos claros. */
    val ink: Color,
)

val AppColors =
    BwColors(
        background = Color(0xFF083B38),
        surface = Color(0x29F7F2EC),
        outline = Color(0x57F7F2EC),
        content = Color(0xFFF7F2EC),
        contentSoft = Color(0xFFE2D7CC),
        accent = Color(0xFFFFB08A),
        onAccent = Color(0xFF1B1613),
        primary = Color(0xFFF7F2EC),
        onPrimary = Color(0xFF1B1613),
        info = Color(0xFF7FE0D4),
        track = Color(0x47F7F2EC),
        stateTaken = Color(0xFF8FE3B0),
        stateDue = Color(0xFFFFB08A),
        stateMissed = Color(0xFFFF8A70),
        stateSkipped = Color(0xFFC9CFCD),
        ink = Color(0xFF1B1613),
    )

/** Degradados tonales de fondo (mismo matiz, de arriba a abajo): uno por papel de la pantalla. */
object BwTones {
    val petrol = listOf(Color(0xFF083B38), Color(0xFF0E766E))
    val alert = listOf(Color(0xFF3F1408), Color(0xFFB8401C))
    val done = listOf(Color(0xFF0D3A1F), Color(0xFF2A7A35))
}

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

    /** Discos de acción: el principal (Tomada) y los secundarios (Omitir, Posponer). */
    val discMain = 60.dp
    val discSecondary = 48.dp
    val discIcon = 26.dp

    /** Botón redondo de esquina: visual de 36 dp dentro de un área táctil de 48 dp. */
    val corner = 36.dp

    /** Espacio extra arriba cuando la pantalla lleva botones de esquina. */
    val cornerClearance = 20.dp

    /** Margen lateral del dato central para no pisar los números de la esfera. */
    val dialClearance = 30.dp
    val badgeIcon = 32.dp
    val barChart = 48.dp
    val bar = 5.dp
}
