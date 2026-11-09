package com.smartwatch.recordatorios.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.Sizes
import com.smartwatch.recordatorios.ui.theme.Spacing

/**
 * Botón del borde: la acción principal de la pantalla, abajo del todo. Arriba es plano con las
 * esquinas redondas y abajo sigue la curva de la pantalla, así que nunca se ve cortado. Icono y una
 * palabra; para TalkBack es un botón con esa palabra.
 */
@Composable
fun BwEdgeButton(
    label: String,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
    container: Color,
    content: Color,
    modifier: Modifier = Modifier,
) {
    val screen = LocalConfiguration.current.screenWidthDp.dp
    val shape = remember(screen) { EdgeShape(screen) }
    Box(
        modifier =
            modifier
                .width(screen * WIDTH_FRACTION)
                .height(Sizes.edgeButton)
                .clip(shape)
                .background(container)
                .clickable(role = Role.Button, onClick = onClick)
                .semantics(mergeDescendants = true) {}
                .padding(top = Spacing.md),
        contentAlignment = Alignment.TopCenter,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BwIcon(icon, contentDescription = null, color = content, modifier = Modifier.size(Sizes.icon))
            BwText(label, style = BwTheme.type.label, color = content, maxLines = 1)
        }
    }
}

/** Rectángulo de esquinas superiores redondas, recortado por el círculo de la pantalla. */
private class EdgeShape(
    private val screen: Dp,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val corner = with(density) { Sizes.edgeButtonCorner.toPx() }
        val radius = with(density) { screen.toPx() } / 2
        val top =
            Path().apply {
                addRoundRect(
                    RoundRect(
                        rect = Rect(Offset.Zero, size),
                        topLeft = CornerRadius(corner),
                        topRight = CornerRadius(corner),
                    ),
                )
            }
        // El botón apoya en el borde inferior: el centro de la pantalla queda un radio por encima.
        val circle =
            Path().apply {
                addOval(Rect(Offset(size.width / 2, size.height - radius), radius))
            }
        return Outline.Generic(Path.combine(PathOperation.Intersect, top, circle))
    }
}

private const val WIDTH_FRACTION = 0.74f
