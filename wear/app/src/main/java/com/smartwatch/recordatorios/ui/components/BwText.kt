package com.smartwatch.recordatorios.ui.components

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.smartwatch.recordatorios.ui.theme.BwTheme

/** Texto del tema: color de contenido del modo y estilos de [BwTheme.type]. */
@Composable
fun BwText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = BwTheme.type.body,
    color: Color = BwTheme.colors.content,
    textAlign: TextAlign = TextAlign.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = style.merge(color = color, textAlign = textAlign),
        overflow = TextOverflow.Ellipsis,
        maxLines = maxLines,
    )
}
