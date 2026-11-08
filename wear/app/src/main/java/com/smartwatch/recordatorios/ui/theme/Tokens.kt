package com.smartwatch.recordatorios.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Primitivos: la paleta de Recuerda, con los mismos nombres y valores que el panel web (`--p-*` en
 * web/src/design/tokens/tokens.css). Solo los leen los papeles de abajo ([AppColors],
 * [BwBackgrounds], [medicationColor]); ninguna pantalla ni componente usa un primitivo directamente.
 * Los que hacen falta en XML están repetidos en res/values/colors.xml y una prueba vigila que sigan
 * iguales.
 */
object Palette {
    val petrol900 = Color(0xFF083B38)
    val petrol600 = Color(0xFF0B605A)
    val cream = Color(0xFFF7F2EC)
    val sand = Color(0xFFE2D7CC)
    val ink = Color(0xFF1B1613)
    val aqua = Color(0xFF7FE0D4)
    val mint = Color(0xFF8FE3B0)
    val coralLight = Color(0xFFFFB08A)
    val coral = Color(0xFFFF8A70)
    val stone = Color(0xFFC9CFCD)

    /** Degradado de la alarma y de «Tomada». */
    val ember900 = Color(0xFF3F1408)
    val ember500 = Color(0xFFB8401C)
    val forest900 = Color(0xFF0D3A1F)
    val forest500 = Color(0xFF2A7A35)

    /** Brillos de franja (los de los paneles web). */
    val glowMorning = Color(0xFFF6DE68)
    val glowAfternoon = Color(0xFFF99654)
    val glowNight = Color(0xFFDE96C8)
    val glowComplete = Color(0xFF96CE96)

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

/** Opacidades del crema sobre petróleo: vidrio de superficies, contorno y vía de anillos y barras. */
private const val GLASS_ALPHA = 0x29 / 255f
private const val OUTLINE_ALPHA = 0x57 / 255f
private const val TRACK_ALPHA = 0x47 / 255f

/**
 * Papeles de color del reloj: esferas a sangre con cuatro colores como máximo.
 * - Petróleo (principal): el fondo de casi todas las pantallas, en degradado tonal ([BwBackgrounds]).
 * - Aguamarina (su complemento claro): arcos, «ahora» y lo pendiente ([info]).
 * - Naranja rojizo: la alarma y lo que pide atención ([stateDue], [stateMissed]).
 * - Verde: Tomada ([stateTaken]).
 * Lo demás es neutro: el claro cálido de texto, la tinta para lo que va sobre rellenos claros y el
 * vidrio (claro translúcido) de superficies y botones secundarios. Ni negro ni blanco puros.
 * La acción principal es un relleno claro con tinta encima: contrasta sobre cualquier fondo.
 * Contrastes (WCAG): claro sobre el extremo claro de cada degradado ≥ 4,8:1 y texto suave ≥ 5,5:1
 * (≥ 4,69:1 con el brillo de cualquier franja); tinta sobre la acción principal 16:1, sobre
 * aguamarina 11,5:1, sobre verde claro 11,8:1, sobre naranja claro 10,1:1 y sobre rojo claro 7,8:1.
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
        background = Palette.petrol900,
        surface = Palette.cream.copy(alpha = GLASS_ALPHA),
        outline = Palette.cream.copy(alpha = OUTLINE_ALPHA),
        content = Palette.cream,
        contentSoft = Palette.sand,
        accent = Palette.coralLight,
        onAccent = Palette.ink,
        primary = Palette.cream,
        onPrimary = Palette.ink,
        info = Palette.aqua,
        track = Palette.cream.copy(alpha = TRACK_ALPHA),
        stateTaken = Palette.mint,
        stateDue = Palette.coralLight,
        stateMissed = Palette.coral,
        stateSkipped = Palette.stone,
        ink = Palette.ink,
    )

/** Papel de la pantalla: decide su fondo ([BwBackgrounds]). */
enum class BwTone { PETROL, ALERT, DONE, MORNING, AFTERNOON, NIGHT, COMPLETE }

/** La franja del momento: 5 a 12 mañana, 12 a 19 tarde, el resto noche (las mismas que el panel web). */
fun bandTone(epochMillis: Long): BwTone {
    val hour =
        java.time.Instant
            .ofEpochMilli(epochMillis)
            .atZone(java.time.ZoneId.systemDefault())
            .hour
    return when (hour) {
        in MORNING_START until AFTERNOON_START -> BwTone.MORNING
        in AFTERNOON_START until NIGHT_START -> BwTone.AFTERNOON
        else -> BwTone.NIGHT
    }
}

/** Fondo de una pantalla: degradado tonal de arriba a abajo y, en las franjas, su brillo. */
@Immutable
data class BwBackground(
    val gradient: List<Color>,
    val glow: Color? = null,
)

/**
 * Papeles de fondo: el componente pide un [BwTone] y aquí se resuelve con la paleta. El petróleo
 * termina en [Palette.petrol600] para que el texto suave dé ≥ 5,5:1 abajo.
 */
object BwBackgrounds {
    private val petrol = listOf(Palette.petrol900, Palette.petrol600)

    fun of(tone: BwTone): BwBackground =
        when (tone) {
            BwTone.PETROL -> BwBackground(petrol)
            BwTone.ALERT -> BwBackground(listOf(Palette.ember900, Palette.ember500))
            BwTone.DONE -> BwBackground(listOf(Palette.forest900, Palette.forest500))
            BwTone.MORNING -> BwBackground(petrol, Palette.glowMorning)
            BwTone.AFTERNOON -> BwBackground(petrol, Palette.glowAfternoon)
            BwTone.NIGHT -> BwBackground(petrol, Palette.glowNight)
            BwTone.COMPLETE -> BwBackground(petrol, Palette.glowComplete)
        }
}

private const val MORNING_START = 5
private const val AFTERNOON_START = 12
private const val NIGHT_START = 19

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

/** Escala tipográfica: nada por debajo de 14 sp; el dato que se mira de un vistazo a 44 sp. */
object TypeScale {
    val hero = 44.sp
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

    /**
     * Anillo del día por el borde: pegado al bisel para pasar por fuera de los puntos del deslizador
     * de páginas (abajo); arriba deja hueco a la hora.
     */
    val ringInset = 1.dp
    val ringStroke = 5.dp
    val progressStroke = 4.dp
    val successMark = 64.dp

    /** Discos de acción: el principal (Tomada) y los secundarios (Omitir, Posponer). */
    val discMain = 60.dp
    val discSecondary = 48.dp
    val discIcon = 26.dp

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
}
