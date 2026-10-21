package com.smartwatch.recordatorios.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.wear.compose.foundation.CurvedTextStyle
import androidx.wear.compose.foundation.basicCurvedText
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnScope
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnState
import androidx.wear.compose.foundation.pager.HorizontalPager
import androidx.wear.compose.foundation.pager.PagerState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.HorizontalPageIndicator
import androidx.wear.compose.material3.HorizontalPagerScaffold
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.ScrollIndicator
import androidx.wear.compose.material3.ScrollIndicatorDefaults
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.TimeText
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.Glow
import com.smartwatch.recordatorios.ui.theme.TypeScale

/**
 * AppScaffold de Wear (hora, transiciones) con los colores del tema propio y el difuminado púrpura
 * de fondo en todas las pantallas.
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
    ) {
        Box(
            modifier =
                Modifier.fillMaxSize().drawBehind {
                    drawRect(
                        Brush.radialGradient(
                            0f to colors.glowCenter,
                            Glow.MID_STOP to colors.glowMid,
                            Glow.END_STOP to colors.background,
                            center = Offset(size.width / 2, size.height * Glow.CENTER_Y),
                            radius = size.minDimension * Glow.RADIUS,
                        ),
                    )
                },
            content = content,
        )
    }
}

/**
 * Páginas horizontales con indicador de página en los colores del tema. La corona desplaza la lista
 * de la página visible, no cambia de página.
 */
@Composable
fun BwPager(
    pagerState: PagerState,
    page: @Composable (Int) -> Unit,
) {
    val colors = BwTheme.colors
    HorizontalPagerScaffold(
        pagerState = pagerState,
        pageIndicator = {
            HorizontalPageIndicator(
                pagerState = pagerState,
                selectedColor = colors.content,
                unselectedColor = colors.track,
                backgroundColor = Color.Transparent,
            )
        },
    ) {
        HorizontalPager(state = pagerState, rotaryScrollableBehavior = null) { page(it) }
    }
}

/**
 * Elemento de lista que se encoge y se desvanece al acercarse al borde superior o inferior, para que
 * la pantalla redonda no lo corte. Usar en lugar de `item` en toda lista del reloj.
 */
fun TransformingLazyColumnScope.edgeItem(
    key: Any? = null,
    content: @Composable () -> Unit,
) = item(key = key) {
    val spec = rememberTransformationSpec()
    val transformation = SurfaceTransformation(spec)
    Box(
        modifier =
            Modifier.fillMaxWidth().transformedHeight(this, spec).graphicsLayer {
                with(transformation) {
                    applyContentTransformation()
                    applyContainerTransformation()
                }
            },
        contentAlignment = Alignment.Center,
    ) { content() }
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
                colors = ScrollIndicatorDefaults.colors(indicatorColor = colors.content, trackColor = colors.track),
            )
        },
        content = content,
    )
}
