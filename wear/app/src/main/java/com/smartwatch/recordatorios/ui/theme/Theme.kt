package com.smartwatch.recordatorios.ui.theme

import android.app.Activity
import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.smartwatch.recordatorios.data.local.ThemeMode

/** Tema propio (sin Material): colores por modo, tipografía y formas en un solo lugar. */
object BwTheme {
    val colors: BwColors
        @Composable @ReadOnlyComposable
        get() = LocalBwColors.current

    val type = BwTypography

    val shapes = BwShapes
}

object BwTypography {
    val data = TextStyle(fontSize = TypeScale.data, fontWeight = FontWeight.Bold)
    val title = TextStyle(fontSize = TypeScale.title, fontWeight = FontWeight.Bold)
    val body = TextStyle(fontSize = TypeScale.body)
    val label = TextStyle(fontSize = TypeScale.body, fontWeight = FontWeight.Bold)
    val support = TextStyle(fontSize = TypeScale.support)
    val code = TextStyle(fontSize = TypeScale.code, fontWeight = FontWeight.Bold, fontFeatureSettings = "tnum")
}

object BwShapes {
    val pill = RoundedCornerShape(percent = 50)
    val card = RoundedCornerShape(Sizes.cardRadius)
}

private val LocalBwColors = staticCompositionLocalOf { DarkColors }

private fun colorsFor(mode: ThemeMode): BwColors = if (mode == ThemeMode.LIGHT) LightColors else DarkColors

/** Fondo de ventana del modo elegido: sin esto, al abrir en claro se ve el fondo oscuro del manifiesto. */
fun Activity.applyWindowBackground(mode: ThemeMode) {
    window.setBackgroundDrawable(ColorDrawable(colorsFor(mode).background.toArgb()))
}

@Composable
fun RecordatoriosTheme(
    mode: ThemeMode = ThemeMode.DARK,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalBwColors provides colorsFor(mode), content = content)
}
