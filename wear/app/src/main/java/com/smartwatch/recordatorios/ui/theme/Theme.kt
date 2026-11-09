package com.smartwatch.recordatorios.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.unit.em
import com.smartwatch.recordatorios.R

/** Tema propio (sin Material): colores, tipografía y formas en un solo lugar. Un único modo oscuro. */
object BwTheme {
    val colors = AppColors

    val backgrounds = BwBackgrounds

    val type = BwTypography

    val shapes = BwShapes
}

/**
 * La misma letra que el panel web: Inter Tight, grotesca apretada para cifras grandes y titulares
 * cortos. Fuente variable empaquetada (OFL, licencia en assets); cada peso fija su eje `wght`.
 */
private fun interTight(weight: Int) =
    Font(
        R.font.inter_tight,
        FontWeight(weight),
        variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
    )

val AppFont = FontFamily(interTight(REGULAR), interTight(MEDIUM), interTight(SEMI_BOLD), interTight(BOLD))

/**
 * Fraunces para la hora y los títulos: una serifa suave, como la etiqueta de una farmacia. Variable
 * (OFL, licencia en assets) con el eje SOFT al máximo para que las serifas se redondeen.
 */
private fun fraunces(
    weight: Int,
    opticalSize: Float,
) = Font(
    R.font.fraunces,
    FontWeight(weight),
    variationSettings =
        FontVariation.Settings(
            FontVariation.weight(weight),
            FontVariation.Setting("opsz", opticalSize),
            FontVariation.Setting("SOFT", SOFT_MAX),
        ),
)

val DisplayFont = FontFamily(fraunces(BOLD, OPSZ_DISPLAY))
val TitleFont = FontFamily(fraunces(BOLD, OPSZ_TITLE))

private const val SOFT_MAX = 100f
private const val OPSZ_DISPLAY = 144f
private const val OPSZ_TITLE = 48f
private const val REGULAR = 400
private const val MEDIUM = 500
private const val BOLD = 700
private const val SEMI_BOLD = 600

/** Sin guiones: una palabra larga baja de línea entera. */
private val Base = TextStyle(fontFamily = AppFont, hyphens = Hyphens.None, lineBreak = LineBreak.Paragraph)

object BwTypography {
    /** Títulos y nombres de medicamento, en Fraunces. */
    val title = Base.copy(fontFamily = TitleFont, fontSize = TypeScale.title, fontWeight = FontWeight.Bold)
    val body = Base.copy(fontSize = TypeScale.body)
    val label = Base.copy(fontSize = TypeScale.body, fontWeight = FontWeight.SemiBold)
    val support = Base.copy(fontSize = TypeScale.support)

    /** La hora o la cifra que se mira de un vistazo: Fraunces grande, con cifras de ancho fijo. */
    val display =
        Base.copy(
            fontFamily = DisplayFont,
            fontSize = TypeScale.hero,
            fontWeight = FontWeight.Bold,
            letterSpacing = DISPLAY_TIGHT.em,
            fontFeatureSettings = "tnum",
        )

    /** Para [display] con autoSize: de 50 sp hasta el tamaño de título si no cabe. */
    val displayFit = TextAutoSize.StepBased(minFontSize = TypeScale.title, maxFontSize = TypeScale.hero)

    /** Para [title] con autoSize: un nombre de una sola palabra baja hasta 14 sp antes de partirse. */
    val titleFit = TextAutoSize.StepBased(minFontSize = TypeScale.support, maxFontSize = TypeScale.title)

    /** Para una dirección web en una sola línea: de 16 sp hasta 12 sp antes de cortarse. */
    val urlFit = TextAutoSize.StepBased(minFontSize = TypeScale.url, maxFontSize = TypeScale.body)
    val code = Base.copy(fontSize = TypeScale.code, fontWeight = FontWeight.SemiBold, fontFeatureSettings = "tnum")
}

object BwShapes {
    val pill = RoundedCornerShape(percent = 50)
    val card = RoundedCornerShape(Sizes.cardRadius)

    /** Esquina corta de placa de señal (pictograma, estado, franja). */
    val plate = RoundedCornerShape(Sizes.plateRadius)
}

@Composable
fun RecordatoriosTheme(content: @Composable () -> Unit) {
    content()
}

private const val DISPLAY_TIGHT = -0.02f
