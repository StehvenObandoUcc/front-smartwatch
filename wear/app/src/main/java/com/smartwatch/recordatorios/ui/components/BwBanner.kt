package com.smartwatch.recordatorios.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.Hyphens
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.LARGE_FONT_SCALE
import com.smartwatch.recordatorios.ui.theme.Sizes
import com.smartwatch.recordatorios.ui.theme.Spacing

/**
 * Aviso compacto: icono y título en el color de acento sobre superficie y una explicación
 * corta (escrita para caber en dos líneas; con letra grande ocupa las que necesite).
 * Nunca solo color: siempre icono (con [iconDescription] para TalkBack) y título.
 * Sin [onClick] es un aviso informativo, no clicable.
 */
@Composable
fun BwBanner(
    @DrawableRes icon: Int,
    iconDescription: String,
    title: String,
    modifier: Modifier = Modifier,
    body: String? = null,
    onClick: (() -> Unit)? = null,
) {
    val colors = BwTheme.colors
    val text = @Composable {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            // Títulos cortos: sin guiones (con letra grande el icono se apila y la palabra cabe).
            BwText(title, style = BwTheme.type.label.copy(hyphens = Hyphens.None), color = colors.accent)
            body?.let { BwText(it, style = BwTheme.type.support, color = colors.contentSoft) }
        }
    }
    val iconView = @Composable { BwIcon(icon, contentDescription = iconDescription, color = colors.accent) }
    // Con letra grande el icono va encima: a su lado el título no cabe y partiría las palabras.
    val stacked = LocalDensity.current.fontScale >= LARGE_FONT_SCALE
    Row(
        modifier =
            modifier
                .heightIn(min = Sizes.touchMin)
                .clip(BwTheme.shapes.card)
                .background(colors.surface)
                .then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier)
                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        if (stacked) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                iconView()
                text()
            }
        } else {
            iconView()
            text()
        }
    }
}
