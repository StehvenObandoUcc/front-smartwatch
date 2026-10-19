package com.smartwatch.recordatorios.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.Spacing

/** Título de pantalla centrado; TalkBack lo anuncia como encabezado. */
@Composable
fun BwHeader(
    text: String,
    modifier: Modifier = Modifier,
) {
    BwText(
        text = text,
        style = BwTheme.type.title,
        textAlign = TextAlign.Center,
        maxLines = 2,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.sm)
                .semantics { heading() },
    )
}
