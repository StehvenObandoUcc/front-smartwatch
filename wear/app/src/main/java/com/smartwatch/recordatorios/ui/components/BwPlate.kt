package com.smartwatch.recordatorios.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import com.smartwatch.recordatorios.R
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.Sizes
import com.smartwatch.recordatorios.ui.theme.Spacing

/** Por encima de esta luminancia la placa es clara y su pictograma va en tinta, no en blanco. */
private const val LIGHT_PLATE = 0.4f

/** Tinta o blanco según el color de la placa, para que el pictograma se lea siempre. */
fun onPlate(color: Color): Color = if (color.luminance() > LIGHT_PLATE) BwTheme.colors.ink else BwTheme.colors.content

/**
 * Placa de pictograma, la firma del sistema (la misma que en el panel web): cuadrada, esquina corta,
 * un icono. Decorativa: quien la usa pone la palabra al lado.
 */
@Composable
fun BwPlate(
    @DrawableRes icon: Int,
    color: Color,
    modifier: Modifier = Modifier,
    small: Boolean = false,
) {
    Box(
        modifier =
            modifier
                .size(if (small) Sizes.plateSmall else Sizes.plate)
                .background(color, BwTheme.shapes.plate),
        contentAlignment = Alignment.Center,
    ) {
        BwIcon(
            icon,
            contentDescription = null,
            color = onPlate(color),
            modifier = Modifier.size(if (small) Sizes.plateSmallIcon else Sizes.plateIcon),
        )
    }
}

/** Estado de una dosis como placa: pictograma y palabra sobre su color (Tomada, Pendiente…). */
enum class DoseStateTone { TAKEN, DUE, MISSED, SKIPPED }

@Composable
fun BwStatePlate(
    tone: DoseStateTone,
    label: String,
    modifier: Modifier = Modifier,
) {
    val colors = BwTheme.colors
    val (color, icon) =
        when (tone) {
            DoseStateTone.TAKEN -> colors.stateTaken to R.drawable.ic_check
            DoseStateTone.DUE -> colors.stateDue to R.drawable.ic_snooze
            DoseStateTone.MISSED -> colors.stateMissed to R.drawable.ic_close
            DoseStateTone.SKIPPED -> colors.stateSkipped to R.drawable.ic_skipped
        }
    Row(
        modifier =
            modifier
                .background(color, BwTheme.shapes.plate)
                .padding(horizontal = Spacing.sm, vertical = Spacing.xs)
                .semantics(mergeDescendants = true) {},
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        BwIcon(
            icon,
            contentDescription = null,
            color = onPlate(color),
            modifier = Modifier.size(Sizes.plateSmallIcon),
        )
        BwText(label, style = BwTheme.type.support, color = onPlate(color), maxLines = 1)
    }
}

/**
 * Franja del día como señal compacta de su color: sol, tarde o noche, con su palabra. No es un botón:
 * mide lo justo para la palabra y no ocupa la pantalla a lo ancho.
 */
enum class DayPart { MORNING, AFTERNOON, NIGHT }

@Composable
fun BwSignBar(
    part: DayPart,
    label: String,
    modifier: Modifier = Modifier,
) {
    val colors = BwTheme.colors
    // Las franjas no tienen color propio: las distinguen el pictograma y la palabra, sobre vidrio.
    val icon =
        when (part) {
            DayPart.MORNING -> R.drawable.ic_sun
            DayPart.AFTERNOON -> R.drawable.ic_sunset
            DayPart.NIGHT -> R.drawable.ic_moon
        }
    val color = colors.surface
    val tint = colors.content
    Row(
        modifier =
            modifier
                .heightIn(min = Sizes.signBar)
                .background(color, BwTheme.shapes.pill)
                .padding(horizontal = Spacing.md, vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm, Alignment.CenterHorizontally),
    ) {
        BwIcon(icon, contentDescription = null, color = tint)
        BwText(label, style = BwTheme.type.label, color = tint, textAlign = TextAlign.Center)
    }
}
