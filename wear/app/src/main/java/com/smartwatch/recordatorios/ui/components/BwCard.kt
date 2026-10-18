package com.smartwatch.recordatorios.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.Sizes
import com.smartwatch.recordatorios.ui.theme.Spacing

/**
 * Tarjeta clicable: superficie con borde de 2 dp y contenido centrado (pantalla redonda).
 * Al pulsar, la superficie pasa al tono del fondo; el borde se mantiene.
 */
@Composable
fun BwCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onClickLabel: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = BwTheme.colors
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    Column(
        modifier =
            modifier
                .heightIn(min = Sizes.touchMin)
                .clip(BwTheme.shapes.card)
                .background(if (pressed) colors.background else colors.surface)
                .border(Sizes.border, colors.content, BwTheme.shapes.card)
                .clickable(
                    interactionSource = interaction,
                    indication = null,
                    onClickLabel = onClickLabel,
                    role = Role.Button,
                    onClick = onClick,
                ).padding(horizontal = Spacing.lg, vertical = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content,
    )
}
