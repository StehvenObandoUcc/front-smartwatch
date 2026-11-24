package com.smartwatch.recordatorios.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Primitivos: la paleta de Recuerda en el reloj. Solo los leen los papeles de abajo ([AppColors],
 * [TileColors], [BwBackgrounds], [medicationColor]) y Tomi, que tiene sus colores fijos en cualquier
 * fondo. Ninguna pantalla los usa directamente. Los que hacen falta en XML están repetidos en
 * res/values/colors.xml y una prueba vigila que sigan iguales.
 */
object Palette {
    /** Fondo de la pantalla: salvia muy clara, como el papel de un pastillero. */
    val screen = Color(0xFFE6EFE1)
    val white = Color(0xFFFFFFFF)
    val sage = Color(0xFFA8D49A)
    val sageDeep = Color(0xFF3B6E33)
    val ink = Color(0xFF1F2A1C)
    val inkSoft = Color(0xFF3F4D37)
    val coral = Color(0xFFF4B49E)
    val graphite = Color(0xFF2B2F2A)
    val stone = Color(0xFFC9D3C4)

    /** Tonos de medicamento: profundos, con el pictograma claro legible encima (≥ 3:1). */
    val medRed = Color(0xFFC62828)
    val medOrange = Color(0xFFD4661A)
    val medYellow = Color(0xFFB08900)
    val medGreen = Color(0xFF2E7D32)
    val medTeal = Color(0xFF00796B)
    val medBlue = Color(0xFF1565C0)
    val medPurple = Color(0xFF6A1B9A)
    val medPink = Color(0xFFAD1457)
}

/** Opacidades de la tinta sobre el fondo claro: contorno de botones secundarios y vía de anillos. */
private const val OUTLINE_ALPHA = 0x2E / 255f
private const val TRACK_ALPHA = 0x26 / 255f

/**
 * Papeles de color del reloj, en tema claro:
 * - Salvia: lo hecho («Tomada») y la acción principal ([primary], [stateTaken]).
 * - Salvia profunda: lo que toca ahora ([info], [stateDue]).
 * - Coral: solo la alarma ([BwTone.ALERT]).
 * - Grafito: lo que no se deshace (omitir, desvincular) y los avisos ([accent], [stateMissed]).
 * Texto en tinta; tarjetas y botones secundarios en blanco.
 * Contrastes (WCAG): tinta sobre el fondo 12,7:1, sobre salvia 8,9:1 y sobre coral 8,4:1; texto suave
 * sobre el fondo 7,6:1 y sobre coral 5,1:1; blanco sobre grafito 13,6:1 y sobre salvia profunda 6,1:1.
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
    /** Texto e iconos sobre los rellenos oscuros (grafito, salvia profunda, colores de medicamento). */
    val onDark: Color,
)

val AppColors =
    BwColors(
        background = Palette.screen,
        surface = Palette.white,
        outline = Palette.ink.copy(alpha = OUTLINE_ALPHA),
        content = Palette.ink,
        contentSoft = Palette.inkSoft,
        accent = Palette.graphite,
        onAccent = Palette.white,
        primary = Palette.sage,
        onPrimary = Palette.ink,
        info = Palette.sageDeep,
        track = Palette.ink.copy(alpha = TRACK_ALPHA),
        stateTaken = Palette.sage,
        stateDue = Palette.sageDeep,
        stateMissed = Palette.graphite,
        stateSkipped = Palette.stone,
        ink = Palette.ink,
        onDark = Palette.white,
    )

/**
 * El Tile se pinta sobre el negro del sistema, no sobre el fondo de la app: texto claro y salvia.
 * Claro sobre negro 17,8:1, salvia 12,5:1.
 */
object TileColors {
    val content = Palette.screen
    val contentSoft = Palette.sage
    val track = Palette.screen.copy(alpha = TILE_TRACK_ALPHA)
    val taken = Palette.sage
}

private const val TILE_TRACK_ALPHA = 0x47 / 255f

/** Papel de la pantalla: decide su fondo ([BwBackgrounds]). */
enum class BwTone { PLAIN, ALERT }

/** Fondo plano en todas las pantallas; la alarma es la única en coral. */
object BwBackgrounds {
    fun of(tone: BwTone): Color =
        when (tone) {
            BwTone.PLAIN -> Palette.screen
            BwTone.ALERT -> Palette.coral
        }
}

/** Claves de color de la API (las del panel web) y su tono de la paleta. */
private val medicationPalette =
    mapOf(
        "red" to Palette.medRed,
        "orange" to Palette.medOrange,
        "yellow" to Palette.medYellow,
        "green" to Palette.medGreen,
        "teal" to Palette.medTeal,
        "blue" to Palette.medBlue,
        "purple" to Palette.medPurple,
        "pink" to Palette.medPink,
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

/** Escala tipográfica: nada por debajo de 14 sp; la hora que se mira de un vistazo a 50 sp. */
object TypeScale {
    val hero = 50.sp
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
    val successMark = 44.dp

    /** Margen lateral del dato central para no pisar los números de la esfera. */
    val dialClearance = 30.dp

    /** Calendario del mes: casilla de cada día, su icono y el aro de hoy. */
    val calendarCell = 24.dp
    val calendarIcon = 14.dp
    val todayRing = 2.dp

    /** Punto de cada día en la semana del mes: cabe 7 veces en el ancho del círculo. */
    val weekDot = 18.dp
    val weekIcon = 12.dp

    /** Icono de estado en una fila del historial. */
    val historyIcon = 24.dp
    val badgeIcon = 32.dp
    val barChart = 48.dp
    val bar = 5.dp

    /** Pastillero: casilla de cada dosis del día, su esquina, el borde de la siguiente y el visto. */
    val pillCell = 24.dp
    val pillCellRadius = 7.dp
    val pillNextStroke = 2.5.dp
    val pillGap = 5.dp

    /** Punto de color del medicamento junto a su nombre, con borde de tinta. */
    val medDot = 12.dp
    val medDotBorder = 2.dp

    /** Tomi: ancho de la cápsula (alto = 5/8) en la alarma y en las pantallas de estado. */
    val tomi = 84.dp
    val tomiSmall = 72.dp

    /** Botón del borde: alto libre al final de la lista, alto del botón y esquina superior. */
    val edgeButtonRoom = 64.dp
    val edgeButton = 58.dp
    val edgeButtonCorner = 22.dp
}
