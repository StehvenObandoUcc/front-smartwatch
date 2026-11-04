package com.smartwatch.recordatorios.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.CurvedTextStyle
import androidx.wear.compose.foundation.basicCurvedText
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnScope
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnState
import androidx.wear.compose.foundation.pager.HorizontalPager
import androidx.wear.compose.foundation.pager.PagerState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.CardDefaults
import androidx.wear.compose.material3.CompactButtonDefaults
import androidx.wear.compose.material3.HorizontalPageIndicator
import androidx.wear.compose.material3.HorizontalPagerScaffold
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.ScrollIndicator
import androidx.wear.compose.material3.ScrollIndicatorDefaults
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.TimeText
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import com.smartwatch.recordatorios.ui.theme.AppFont
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.BwTone
import com.smartwatch.recordatorios.ui.theme.TypeScale

/**
 * AppScaffold de Wear (hora, transiciones) con los colores del tema propio, sobre fondo plano.
 */
@Composable
fun BwAppScaffold(content: @Composable BoxScope.() -> Unit) {
    val colors = BwTheme.colors
    AppScaffold(
        timeText = {
            TimeText(backgroundColor = Color.Transparent) { time ->
                basicCurvedText(
                    time,
                    style = {
                        CurvedTextStyle(
                            color = colors.content,
                            fontSize = TypeScale.support,
                            fontFamily = AppFont,
                        )
                    },
                )
            }
        },
        containerColor = colors.background,
        contentColor = colors.content,
    ) {
        Box(modifier = Modifier.fillMaxSize(), content = content)
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
 * Elemento de lista que se desvanece al acercarse al borde superior o inferior, para que
 * la pantalla redonda no lo corte. Usar en lugar de `item` en toda lista del reloj. Si es el primero
 * o el último, deja el margen que pide Wear: mayor con [container] (tarjetas, avisos, botones), donde
 * las esquinas chocarían con el círculo.
 */
fun TransformingLazyColumnScope.edgeItem(
    key: Any? = null,
    container: Boolean = false,
    content: @Composable () -> Unit,
) = item(key = key) {
    val spec = rememberTransformationSpec()
    val transformation = SurfaceTransformation(spec)
    val edgePadding =
        if (container) {
            CardDefaults.minimumVerticalListContentPadding
        } else {
            CompactButtonDefaults.minimumVerticalListContentPadding
        }
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .minimumVerticalContentPadding(
                    edgePadding,
                ).transformedHeight(this, spec)
                .graphicsLayer {
                    // Solo se desvanece el contenido: el contenedor no se curva al llegar al borde.
                    with(transformation) { applyContentTransformation() }
                },
        contentAlignment = Alignment.Center,
    ) { content() }
}

/**
 * ScreenScaffold de Wear (indicador de scroll, rotary) sobre una esfera a sangre: el degradado tonal
 * de su [tone] y un [backdrop] opcional fijo detrás de la lista.
 * Añade un margen lateral del 5,2 % del ancho (la guía de Wear OS) para que nada choque con el borde.
 */
@Composable
fun BwScreenScaffold(
    scrollState: TransformingLazyColumnState,
    tone: BwTone = BwTone.PETROL,
    backdrop: (@Composable BoxScope.() -> Unit)? = null,
    content: @Composable BoxScope.(PaddingValues) -> Unit,
) {
    val colors = BwTheme.colors
    val side = (LocalConfiguration.current.screenWidthDp * EDGE_MARGIN).dp
    val background = BwTheme.backgrounds.of(tone)
    val glow = background.glow
    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(background.gradient))
            .then(if (glow != null) Modifier.drawBehind { bandGlow(glow) } else Modifier),
    ) {
        backdrop?.invoke(this)
        ScreenContent(scrollState, side, content)
    }
}

@Composable
private fun ScreenContent(
    scrollState: TransformingLazyColumnState,
    side: androidx.compose.ui.unit.Dp,
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
    ) { padding ->
        val direction = LocalLayoutDirection.current
        content(
            PaddingValues(
                start = maxOf(padding.calculateStartPadding(direction), side),
                top = padding.calculateTopPadding(),
                end = maxOf(padding.calculateEndPadding(direction), side),
                bottom = padding.calculateBottomPadding(),
            ),
        )
    }
}

private const val EDGE_MARGIN = 0.052f

/**
 * Dos brillos radiales en el borde inferior (derecha e izquierda), la receta de los paneles web
 * llevada al círculo: brillan el bisel y las esquinas, no la zona de texto. Medido: el texto suave
 * queda en 4,69:1 o más en las cuatro franjas.
 */
private fun DrawScope.bandGlow(color: Color) {
    val w = size.width
    val h = size.height
    drawRect(
        Brush.radialGradient(
            listOf(color.copy(alpha = GLOW_STRONG), Color.Transparent),
            center = Offset(w * GLOW_RIGHT_X, h * GLOW_RIGHT_Y),
            radius = w * GLOW_RADIUS,
        ),
    )
    drawRect(
        Brush.radialGradient(
            listOf(color.copy(alpha = GLOW_SOFT), Color.Transparent),
            center = Offset(w * GLOW_LEFT_X, h * GLOW_LEFT_Y),
            radius = w * GLOW_RADIUS,
        ),
    )
}

private const val GLOW_STRONG = 0.3f
private const val GLOW_SOFT = 0.225f
private const val GLOW_RADIUS = 0.5f
private const val GLOW_RIGHT_X = 1f
private const val GLOW_RIGHT_Y = 1f
private const val GLOW_LEFT_X = 0.1f
private const val GLOW_LEFT_Y = 1.15f
