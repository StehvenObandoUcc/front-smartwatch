package com.smartwatch.recordatorios.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

/** Tema propio (sin Material): colores, tipografía y formas en un solo lugar. Un único modo oscuro. */
object BwTheme {
    val colors = AppColors

    val type = BwTypography

    val shapes = BwShapes
}

object BwTypography {
    val title = TextStyle(fontSize = TypeScale.title, fontWeight = FontWeight.Bold)
    val body = TextStyle(fontSize = TypeScale.body)
    val label = TextStyle(fontSize = TypeScale.body, fontWeight = FontWeight.Bold)
    val support = TextStyle(fontSize = TypeScale.support)
    val display = TextStyle(fontSize = TypeScale.hero, fontWeight = FontWeight.Medium, fontFeatureSettings = "tnum")
    val code = TextStyle(fontSize = TypeScale.code, fontWeight = FontWeight.Bold, fontFeatureSettings = "tnum")
}

object BwShapes {
    val pill = RoundedCornerShape(percent = 50)
    val card = RoundedCornerShape(Sizes.cardRadius)
}

@Composable
fun RecordatoriosTheme(content: @Composable () -> Unit) {
    content()
}
