package com.smartwatch.recordatorios.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
 * Cápsula magenta para posponer u omitir: icono arriba y etiqueta debajo, con [detail] opcional
 * (p. ej. los minutos de posponer). Alto mínimo 64 dp; TalkBack lee etiqueta y detalle juntos.
 */
@Composable
fun BwActionPill(
    label: String,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    detail: String? = null,
) {
    val colors = BwTheme.colors
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    Column(
        modifier =
            modifier
                .heightIn(min = Sizes.actionPill)
                .alpha(if (pressed) PRESSED_ALPHA else 1f)
                .clip(BwTheme.shapes.card)
                .background(Brush.verticalGradient(listOf(colors.secondaryStart, colors.secondaryEnd)))
                .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick)
                .padding(Spacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.xs, Alignment.CenterVertically),
    ) {
        BwIcon(icon, contentDescription = null, color = colors.content)
        BwText(label, style = BwTheme.type.label, textAlign = TextAlign.Center, maxLines = 1)
        detail?.let { BwText(it, style = BwTheme.type.support, textAlign = TextAlign.Center, maxLines = 1) }
    }
}
