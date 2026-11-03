package com.smartwatch.recordatorios.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.Sizes
import com.smartwatch.recordatorios.ui.theme.Spacing

/**
 * Aviso compacto dentro del flujo de la pantalla, sin caja de color: el icono en naranja de atención,
 * el título y, si hace falta, una línea de explicación. Si se puede resolver, un solo botón
 * ([actionLabel]) debajo. Nunca solo color: siempre icono (con [iconDescription]) y título.
 */
@Composable
fun BwBanner(
    @DrawableRes icon: Int,
    iconDescription: String,
    title: String,
    modifier: Modifier = Modifier,
    body: String? = null,
    actionLabel: String? = null,
    onClick: (() -> Unit)? = null,
) {
    val colors = BwTheme.colors
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Row(
            modifier = Modifier.semantics(mergeDescendants = true) {},
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            BwIcon(
                icon,
                contentDescription = iconDescription,
                color = colors.accent,
                modifier = Modifier.padding(top = Spacing.xs / 2).size(Sizes.plateIcon),
            )
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs / 2)) {
                BwText(title, style = BwTheme.type.label, color = colors.content)
                body?.let { BwText(it, style = BwTheme.type.support, color = colors.contentSoft) }
            }
        }
        if (onClick != null && actionLabel != null) {
            BwButton(label = actionLabel, onClick = onClick, compact = true)
        }
    }
}
