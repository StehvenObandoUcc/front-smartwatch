package com.smartwatch.recordatorios.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import com.smartwatch.recordatorios.ui.theme.Sizes

/** Icono vectorial teñido con un color del tema; no depende de la fuente del reloj. */
@Composable
fun BwIcon(
    @DrawableRes id: Int,
    contentDescription: String?,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(id),
        contentDescription = contentDescription,
        modifier = modifier.size(Sizes.icon),
        colorFilter = ColorFilter.tint(color),
    )
}
