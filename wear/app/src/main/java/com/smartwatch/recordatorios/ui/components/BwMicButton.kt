package com.smartwatch.recordatorios.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import com.smartwatch.recordatorios.R
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.Sizes
import com.smartwatch.recordatorios.ui.theme.Spacing

private const val OUTER_RING_ALPHA = 0.35f
private const val INNER_RING_ALPHA = 0.6f

/**
 * Micrófono redondo en petróleo plano con dos aros, y su etiqueta debajo
 * (lo que lee TalkBack).
 */
@Composable
fun BwMicButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = BwTheme.colors
    Column(
        modifier = modifier.clickable(role = Role.Button, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Box(
            modifier =
                Modifier.size(Sizes.mic + Sizes.micRing * 2).drawBehind {
                    val ring = Sizes.outline.toPx() * 2
                    drawCircle(colors.info.copy(alpha = OUTER_RING_ALPHA), style = Stroke(ring))
                    drawCircle(
                        colors.info.copy(alpha = INNER_RING_ALPHA),
                        radius = size.minDimension / 2 - Sizes.micRing.toPx() / 2,
                        style = Stroke(ring),
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(Sizes.mic - Sizes.micRing)
                        .clip(CircleShape)
                        .background(colors.primary),
                contentAlignment = Alignment.Center,
            ) {
                BwIcon(R.drawable.ic_mic, contentDescription = null, color = colors.content)
            }
        }
        BwText(label, style = BwTheme.type.label, textAlign = TextAlign.Center, maxLines = 2)
    }
}
