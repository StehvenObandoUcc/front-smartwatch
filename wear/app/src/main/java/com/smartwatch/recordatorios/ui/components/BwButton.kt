package com.smartwatch.recordatorios.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.Sizes
import com.smartwatch.recordatorios.ui.theme.Spacing

private const val PRESSED_ALPHA = 0.8f

/**
 * Botón píldora. Principal: degradado celeste-azul (el del icono) con texto oscuro (una por pantalla).
 * Secundaria: superficie con contorno fino. Alto mínimo 52 dp; el icono es decorativo.
 */
@Composable
fun BwButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    primary: Boolean = false,
    @DrawableRes icon: Int? = null,
) {
    val colors = BwTheme.colors
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val content = if (primary) colors.onPrimary else colors.content
    val fill =
        if (primary) {
            Modifier.background(Brush.horizontalGradient(listOf(colors.primaryStart, colors.primaryEnd)))
        } else {
            Modifier.background(colors.surface).border(Sizes.outline, colors.outline, BwTheme.shapes.pill)
        }
    Row(
        modifier =
            modifier
                .heightIn(min = Sizes.buttonHeight)
                .alpha(if (pressed) PRESSED_ALPHA else 1f)
                .clip(BwTheme.shapes.pill)
                .then(fill)
                .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick)
                .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm, Alignment.CenterHorizontally),
    ) {
        icon?.let { BwIcon(it, contentDescription = null, color = content) }
        BwText(label, style = BwTheme.type.label, color = content, textAlign = TextAlign.Center, maxLines = 2)
    }
}
