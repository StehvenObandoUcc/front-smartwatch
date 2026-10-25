package com.smartwatch.recordatorios.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
import com.smartwatch.recordatorios.ui.theme.AtkinsonNext
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.BwTones
import com.smartwatch.recordatorios.ui.theme.Sizes
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
                            fontFamily = AtkinsonNext,
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
 * Elemento de lista que se encoge y se desvanece al acercarse al borde superior o inferior, para que
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
                    with(transformation) {
                        applyContentTransformation()
                        applyContainerTransformation()
                    }
                },
        contentAlignment = Alignment.Center,
    ) { content() }
}

/** Papel de la pantalla: decide su degradado de fondo (ver [BwTones]). */
enum class BwTone { PETROL, ALERT, DONE }

/** Botones redondos de las esquinas superiores de la esfera (volver, preguntar, agregar…). */
data class BwCorners(
    val start: (@Composable () -> Unit)? = null,
    val end: (@Composable () -> Unit)? = null,
)

/**
 * ScreenScaffold de Wear (indicador de scroll, rotary) sobre una esfera a sangre: el degradado tonal
 * de su [tone], un [backdrop] opcional fijo detrás de la lista (la esfera de 24 h) y los [corners].
 * Añade un margen lateral del 5,2 % del ancho (la guía de Wear OS) para que nada choque con el borde.
 */
@Composable
fun BwScreenScaffold(
    scrollState: TransformingLazyColumnState,
    tone: BwTone = BwTone.PETROL,
    backdrop: (@Composable BoxScope.() -> Unit)? = null,
    corners: BwCorners? = null,
    content: @Composable BoxScope.(PaddingValues) -> Unit,
) {
    val colors = BwTheme.colors
    val side = (LocalConfiguration.current.screenWidthDp * EDGE_MARGIN).dp
    val stops =
        when (tone) {
            BwTone.PETROL -> BwTones.petrol
            BwTone.ALERT -> BwTones.alert
            BwTone.DONE -> BwTones.done
        }
    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(stops))) {
        backdrop?.invoke(this)
        // Con botones de esquina, el contenido empieza más abajo para no quedar debajo de ellos.
        ScreenContent(scrollState, side, if (corners != null) Sizes.cornerClearance else 0.dp, content)
        corners?.let { CornerButtons(it) }
    }
}

@Composable
private fun ScreenContent(
    scrollState: TransformingLazyColumnState,
    side: androidx.compose.ui.unit.Dp,
    extraTop: androidx.compose.ui.unit.Dp,
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
                top = padding.calculateTopPadding() + extraTop,
                end = maxOf(padding.calculateEndPadding(direction), side),
                bottom = padding.calculateBottomPadding(),
            ),
        )
    }
}

/** Las esquinas superiores de una pantalla redonda: a la altura donde el círculo deja sitio. */
@Composable
private fun BoxScope.CornerButtons(corners: BwCorners) {
    val width = LocalConfiguration.current.screenWidthDp
    val inset = (width * CORNER_INSET).dp
    val top = (width * CORNER_TOP).dp
    corners.start?.let { Box(Modifier.align(Alignment.TopStart).padding(start = inset, top = top)) { it() } }
    corners.end?.let { Box(Modifier.align(Alignment.TopEnd).padding(end = inset, top = top)) { it() } }
}

/**
 * Botón redondo de esquina: disco de vidrio de 36 dp con su pictograma, dentro de un área táctil de
 * 48 dp. Siempre con [description] para TalkBack (no tiene palabra visible).
 */
@Composable
fun BwCornerButton(
    @androidx.annotation.DrawableRes icon: Int,
    description: String,
    onClick: () -> Unit,
) {
    val colors = BwTheme.colors
    Box(
        modifier =
            Modifier
                .size(Sizes.touchMin)
                .clickable(onClickLabel = description, role = Role.Button, onClick = onClick)
                .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier.size(Sizes.corner).clip(CircleShape).background(colors.surface),
            contentAlignment = Alignment.Center,
        ) {
            BwIcon(icon, contentDescription = null, color = colors.content, modifier = Modifier.size(Sizes.plateIcon))
        }
    }
}

private const val EDGE_MARGIN = 0.052f
private const val CORNER_INSET = 0.1f
private const val CORNER_TOP = 0.09f
