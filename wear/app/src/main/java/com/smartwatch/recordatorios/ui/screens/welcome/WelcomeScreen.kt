package com.smartwatch.recordatorios.ui.screens.welcome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import com.smartwatch.recordatorios.R
import com.smartwatch.recordatorios.ui.components.BwButton
import com.smartwatch.recordatorios.ui.components.BwHeader
import com.smartwatch.recordatorios.ui.components.BwScreenScaffold
import com.smartwatch.recordatorios.ui.components.BwText
import com.smartwatch.recordatorios.ui.components.edgeItem
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.Spacing

/**
 * Primer arranque: usar el reloj solo (medicamentos creados aquí, sin cuenta ni internet) o
 * vincularlo con el panel web. Cada opción dice en una línea qué implica.
 */
@Composable
fun WelcomeScreen(
    onUseLocally: () -> Unit,
    onPair: () -> Unit,
) {
    val listState = rememberTransformingLazyColumnState()
    BwScreenScaffold(scrollState = listState) { contentPadding ->
        TransformingLazyColumn(
            state = listState,
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            edgeItem { BwHeader(stringResource(R.string.welcome_title)) }
            edgeItem(container = true) {
                BwButton(
                    label = stringResource(R.string.welcome_local),
                    onClick = onUseLocally,
                    primary = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            edgeItem { Explain(stringResource(R.string.welcome_local_body)) }
            edgeItem {
                BwButton(
                    label = stringResource(R.string.welcome_pair),
                    onClick = onPair,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            edgeItem { Explain(stringResource(R.string.welcome_pair_body)) }
        }
    }
}

@Composable
private fun Explain(text: String) {
    BwText(
        text = text,
        style = BwTheme.type.support,
        color = BwTheme.colors.contentSoft,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
}
