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

private const val REGULAR = 400
private const val MEDIUM = 500
private const val BOLD = 700
private const val SEMI_BOLD = 600

/** Sin guiones: una palabra larga baja de línea entera. */
private val Base = TextStyle(fontFamily = AppFont, hyphens = Hyphens.None, lineBreak = LineBreak.Paragraph)

object BwTypography {
    /** Titulares cortos y apretados, como en el panel web. */
    val title = Base.copy(fontSize = TypeScale.title, fontWeight = FontWeight.SemiBold, letterSpacing = TIGHT.em)
    val body = Base.copy(fontSize = TypeScale.body)
    val label = Base.copy(fontSize = TypeScale.body, fontWeight = FontWeight.SemiBold)
    val support = Base.copy(fontSize = TypeScale.support)

    /** La cifra que se mira de un vistazo: grande, en peso medio y muy apretada. */
    val display =
        Base.copy(
            fontSize = TypeScale.hero,
            fontWeight = FontWeight.Medium,
            letterSpacing = DISPLAY_TIGHT.em,
            fontFeatureSettings = "tnum",
        )

    /** Para [display] con autoSize: de 44 sp hasta el tamaño de título si no cabe. */
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

private const val TIGHT = -0.02f
private const val DISPLAY_TIGHT = -0.035f
