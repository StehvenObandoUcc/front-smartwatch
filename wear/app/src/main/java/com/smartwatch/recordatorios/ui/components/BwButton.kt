package com.smartwatch.recordatorios.ui.components

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextAlign
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.Sizes
import com.smartwatch.recordatorios.ui.theme.Spacing

/**
 * Botón píldora. Principal: relleno con el color de contenido. Secundaria: fondo del modo con borde.
 * Al pulsar se invierten relleno y texto. Alto mínimo 52 dp; el icono es decorativo (lo lee la etiqueta).
 */
@Composable
fun BwButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    primary: Boolean = false,
    icon: String? = null,
) {
    val colors = BwTheme.colors
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val filled = primary != pressed
    val container = if (filled) colors.content else colors.background
    val content = if (filled) colors.background else colors.content
    Row(
        modifier =
            modifier
                .heightIn(min = Sizes.buttonHeight)
                .clip(BwTheme.shapes.pill)
                .background(container)
                .border(Sizes.border, colors.content, BwTheme.shapes.pill)
                .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick)
                .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm, Alignment.CenterHorizontally),
    ) {
        icon?.let {
            BwText(it, style = BwTheme.type.title, color = content, modifier = Modifier.clearAndSetSemantics {})
        }
        BwText(label, style = BwTheme.type.label, color = content, textAlign = TextAlign.Center, maxLines = 2)
    }
}
