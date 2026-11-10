package com.smartwatch.recordatorios.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import com.smartwatch.recordatorios.R
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.Sizes
import com.smartwatch.recordatorios.ui.theme.Spacing

/**
 * Fila de una dosis, la de Historial para todo el reloj: el medicamento con su punto de color y debajo
 * la hora (cifras tabulares, en tono suave); a la derecha, el círculo de su estado con icono (TalkBack lee
 * [stateLabel]). Va en una tarjeta blanca con margen interior: por debajo del centro el círculo de la
 * pantalla se estrecha y así no corta el punto ni la hora. Con [onClick] toda la tarjeta se toca.
 */
@Composable
fun BwDoseRow(
    time: String,
    name: String,
    colorKey: String,
    tone: DoseStateTone?,
    stateLabel: String,
    modifier: Modifier = Modifier,
    onClickLabel: String? = null,
    onClick: (() -> Unit)? = null,
) {
    val tap =
        if (onClick != null) {
            Modifier.clickable(onClickLabel = onClickLabel, role = Role.Button, onClick = onClick)
        } else {
            Modifier
        }
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = Sizes.touchMin)
                .clip(BwTheme.shapes.card)
                .background(BwTheme.colors.surface)
                .then(tap)
                .padding(horizontal = Spacing.lg, vertical = Spacing.sm)
                .semantics(mergeDescendants = true) {},
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.xs / 2)) {
            MedicationLabel(name = name, colorKey = colorKey, centered = false, maxLines = 2)
            BwText(
                time,
                style = BwTheme.type.support.merge(fontFeatureSettings = "tnum"),
                color = BwTheme.colors.contentSoft,
                maxLines = 1,
            )
        }
        tone?.let { StateDot(it, stateLabel) }
    }
}

@Composable
private fun StateDot(
    tone: DoseStateTone,
    label: String,
) {
    val colors = BwTheme.colors
    val (fill, icon) =
        when (tone) {
            DoseStateTone.TAKEN -> colors.stateTaken to R.drawable.ic_check
            DoseStateTone.SKIPPED -> colors.stateSkipped to R.drawable.ic_skipped
            DoseStateTone.MISSED -> colors.stateMissed to R.drawable.ic_close
            DoseStateTone.DUE -> colors.stateDue to R.drawable.ic_snooze
        }
    Box(
        modifier = Modifier.size(Sizes.historyIcon).clip(CircleShape).background(fill),
        contentAlignment = Alignment.Center,
    ) {
        BwIcon(icon, contentDescription = label, color = onPlate(fill), modifier = Modifier.size(Sizes.calendarIcon))
    }
}
