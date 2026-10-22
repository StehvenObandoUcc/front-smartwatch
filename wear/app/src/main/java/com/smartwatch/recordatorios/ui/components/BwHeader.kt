package com.smartwatch.recordatorios.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.Spacing

/**
 * Título de pantalla centrado como una señal: la placa petróleo de la sección y su palabra (la misma
 * placa que en el panel web). TalkBack lo anuncia como encabezado.
 */
@Composable
fun BwHeader(
    text: String,
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int? = null,
) {
    val header =
        modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.sm)
            .semantics(mergeDescendants = true) { heading() }
    val plate = @Composable { icon?.let { BwPlate(it, BwTheme.colors.infoDeep) } }
    val title = @Composable {
        BwText(
            text = text,
            style = BwTheme.type.title,
            textAlign = TextAlign.Center,
            maxLines = 2,
        )
    }
    // Un título largo ocupa dos líneas: la placa va encima para no quedar junto a una sola de ellas.
    if (text.length > SHORT_TITLE) {
        Column(
            header,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            plate()
            title()
        }
    } else {
        Row(
            header,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm, Alignment.CenterHorizontally),
        ) {
            plate()
            title()
        }
    }
}

private const val SHORT_TITLE = 14
