package com.smartwatch.recordatorios.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.Sizes
import com.smartwatch.recordatorios.ui.theme.Spacing
import com.smartwatch.recordatorios.ui.theme.medicationColor

/**
 * Punto de color con borde de tinta + nombre: la información nunca va solo por color. Centrados por defecto (pantalla
 * redonda); en filas alineadas a la izquierda, [centered] = false para que el punto no se separe.
 */
@Composable
fun MedicationLabel(
    name: String,
    colorKey: String,
    modifier: Modifier = Modifier,
    maxLines: Int = 2,
    centered: Boolean = true,
) {
    Row(
        modifier = modifier,
        // En filas, el punto va con la primera línea del nombre, no centrado entre dos.
        verticalAlignment = if (centered) Alignment.CenterVertically else Alignment.Top,
        horizontalArrangement =
            Arrangement.spacedBy(Spacing.sm, if (centered) Alignment.CenterHorizontally else Alignment.Start),
    ) {
        // Un punto pequeño: el color distingue el medicamento sin competir con la paleta clara.
        Box(
            Modifier
                .padding(top = if (centered) 0.dp else Spacing.sm)
                .size(Sizes.medDot)
                .clip(CircleShape)
                .background(medicationColor(colorKey))
                .border(Sizes.medDotBorder, BwTheme.colors.ink, CircleShape),
        )
        BwText(
            text = name,
            // Un nombre no se parte nunca a mitad de palabra («Hidroclorotiazid-a»): solo entre
            // palabras. Si es una sola palabra, va en una línea y se achica hasta caber.
            style = BwTheme.type.title.copy(hyphens = Hyphens.None),
            textAlign = if (centered) TextAlign.Center else TextAlign.Start,
            maxLines = if (name.trim().contains(' ')) maxLines else 1,
            autoSize = BwTheme.type.titleFit,
        )
    }
}
