package com.smartwatch.recordatorios.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak

/** Tema propio (sin Material): colores, tipografía y formas en un solo lugar. Un único modo oscuro. */
object BwTheme {
    val colors = AppColors

    val type = BwTypography

    val shapes = BwShapes
}

/** Español con palabras largas («Hidroclorotiazida»): partir con guion, nunca a mitad sin aviso. */
private val Base = TextStyle(hyphens = Hyphens.Auto, lineBreak = LineBreak.Paragraph)

object BwTypography {
    val title = Base.copy(fontSize = TypeScale.title, fontWeight = FontWeight.Bold)
    val body = Base.copy(fontSize = TypeScale.body)
    val label = Base.copy(fontSize = TypeScale.body, fontWeight = FontWeight.Bold)
    val support = Base.copy(fontSize = TypeScale.support)
    val display = Base.copy(fontSize = TypeScale.hero, fontWeight = FontWeight.Medium, fontFeatureSettings = "tnum")

    /** Para [display] con autoSize: de 40 sp hasta el tamaño de título si no cabe. */
    val displayFit = TextAutoSize.StepBased(minFontSize = TypeScale.title, maxFontSize = TypeScale.hero)
    val code = Base.copy(fontSize = TypeScale.code, fontWeight = FontWeight.Bold, fontFeatureSettings = "tnum")
}

object BwShapes {
    val pill = RoundedCornerShape(percent = 50)
    val card = RoundedCornerShape(Sizes.cardRadius)
}

@Composable
fun RecordatoriosTheme(content: @Composable () -> Unit) {
    content()
}
