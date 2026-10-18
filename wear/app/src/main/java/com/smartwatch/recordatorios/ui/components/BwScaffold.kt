package com.smartwatch.recordatorios.ui.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.foundation.CurvedTextStyle
import androidx.wear.compose.foundation.basicCurvedText
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.ScrollIndicator
import androidx.wear.compose.material3.ScrollIndicatorDefaults
import androidx.wear.compose.material3.TimeText
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.TypeScale

/**
 * AppScaffold de Wear (hora, transiciones) con los colores del tema propio: sin MaterialTheme,
 * sus valores por defecto dejarían la hora y el indicador en blanco sobre el modo claro.
 */
@Composable
fun BwAppScaffold(content: @Composable BoxScope.() -> Unit) {
    val colors = BwTheme.colors
    AppScaffold(
        timeText = {
            TimeText(backgroundColor = Color.Transparent) { time ->
                basicCurvedText(time, style = { CurvedTextStyle(color = colors.content, fontSize = TypeScale.support) })
            }
        },
        containerColor = colors.background,
        contentColor = colors.content,
        content = content,
    )
}

/** ScreenScaffold de Wear (indicador de scroll, rotary) con el indicador en los colores del tema. */
@Composable
fun BwScreenScaffold(
    scrollState: TransformingLazyColumnState,
    content: @Composable BoxScope.(PaddingValues) -> Unit,
) {
    val colors = BwTheme.colors
    ScreenScaffold(
        scrollState = scrollState,
        scrollIndicator = {
            ScrollIndicator(
                state = scrollState,
                colors = ScrollIndicatorDefaults.colors(indicatorColor = colors.content, trackColor = colors.surface),
            )
        },
        content = content,
    )
}
