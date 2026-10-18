package com.smartwatch.recordatorios.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.Sizes
import com.smartwatch.recordatorios.ui.theme.Spacing

/**
 * Aviso que exige atención: borde e icono en el color de acento, texto en el de contenido.
 * Nunca solo color: siempre icono y título.
 */
@Composable
fun BwBanner(
    icon: String,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    body: String? = null,
) {
    val colors = BwTheme.colors
    Row(
        modifier =
            modifier
                .heightIn(min = Sizes.touchMin)
                .clip(BwTheme.shapes.card)
                .background(colors.surface)
                .border(Sizes.border, colors.accent, BwTheme.shapes.card)
                .clickable(role = Role.Button, onClick = onClick)
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        BwText(
            icon,
            style = BwTheme.type.data,
            color = colors.accent,
            modifier = Modifier.clearAndSetSemantics {},
        )
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            BwText(title, style = BwTheme.type.label, maxLines = 2)
            body?.let { BwText(it, style = BwTheme.type.support, maxLines = 4) }
        }
    }
}
