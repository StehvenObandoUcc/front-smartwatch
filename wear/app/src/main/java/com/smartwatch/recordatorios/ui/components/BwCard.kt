package com.smartwatch.recordatorios.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.Sizes
import com.smartwatch.recordatorios.ui.theme.Spacing

private const val PRESSED_ALPHA = 0.8f

/**
 * Tarjeta clicable: superficie sin contorno, solo para lo que se toca. Contenido centrado por
 * defecto (pantalla redonda). Al pulsar se atenúa, como los botones.
 */
@Composable
fun BwCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onClickLabel: String? = null,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    content: @Composable ColumnScope.() -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    Column(
        modifier =
            modifier
                .heightIn(min = Sizes.touchMin)
                .alpha(if (pressed) PRESSED_ALPHA else 1f)
                .clip(BwTheme.shapes.card)
                .background(BwTheme.colors.surface)
                .clickable(
                    interactionSource = interaction,
                    indication = null,
                    onClickLabel = onClickLabel,
                    role = Role.Button,
                    onClick = onClick,
                ).padding(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        horizontalAlignment = horizontalAlignment,
        content = content,
    )
}
