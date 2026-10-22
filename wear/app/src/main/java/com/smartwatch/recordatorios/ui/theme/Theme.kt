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
import com.smartwatch.recordatorios.R

/** Tema propio (sin Material): colores, tipografía y formas en un solo lugar. Un único modo oscuro. */
object BwTheme {
    val colors = AppColors

    val type = BwTypography

    val shapes = BwShapes
}

/**
 * La misma letra que el panel web: Atkinson Hyperlegible Next, diseñada para baja visión (distingue
 * 0/O, 1/l/I). Fuente variable empaquetada (OFL, en assets); cada peso fija su eje `wght`.
 */
private fun next(weight: Int) =
    Font(
        R.font.atkinson_hyperlegible_next,
        FontWeight(weight),
        variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
    )

val AtkinsonNext = FontFamily(next(REGULAR), next(MEDIUM), next(BOLD), next(EXTRA_BOLD))

private const val REGULAR = 400
private const val MEDIUM = 500
private const val BOLD = 700
private const val EXTRA_BOLD = 800

/** Español con palabras largas («Hidroclorotiazida»): partir con guion, nunca a mitad sin aviso. */
private val Base = TextStyle(fontFamily = AtkinsonNext, hyphens = Hyphens.Auto, lineBreak = LineBreak.Paragraph)

object BwTypography {
    val title = Base.copy(fontSize = TypeScale.title, fontWeight = FontWeight.ExtraBold)
    val body = Base.copy(fontSize = TypeScale.body)
    val label = Base.copy(fontSize = TypeScale.body, fontWeight = FontWeight.Bold)
    val support = Base.copy(fontSize = TypeScale.support)
    val display = Base.copy(fontSize = TypeScale.hero, fontWeight = FontWeight.Bold, fontFeatureSettings = "tnum")

    /** Para [display] con autoSize: de 40 sp hasta el tamaño de título si no cabe. */
    val displayFit = TextAutoSize.StepBased(minFontSize = TypeScale.title, maxFontSize = TypeScale.hero)
    val code = Base.copy(fontSize = TypeScale.code, fontWeight = FontWeight.ExtraBold, fontFeatureSettings = "tnum")
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
