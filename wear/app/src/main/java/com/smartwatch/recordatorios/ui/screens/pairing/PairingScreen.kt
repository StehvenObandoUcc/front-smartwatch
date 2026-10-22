package com.smartwatch.recordatorios.ui.screens.pairing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import com.smartwatch.recordatorios.R
import com.smartwatch.recordatorios.ui.components.BwButton
import com.smartwatch.recordatorios.ui.components.BwHeader
import com.smartwatch.recordatorios.ui.components.BwProgress
import com.smartwatch.recordatorios.ui.components.BwScreenScaffold
import com.smartwatch.recordatorios.ui.components.BwText
import com.smartwatch.recordatorios.ui.components.edgeItem
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.Sizes
import com.smartwatch.recordatorios.ui.theme.Spacing

@Composable
fun PairingRoute(viewModel: PairingViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    PairingScreen(state = state, onRetry = viewModel::start)
}

/** El reloj no tiene teclado: muestra el código y el usuario lo escribe en el panel web. */
@Composable
fun PairingScreen(
    state: PairingState,
    onRetry: () -> Unit,
) {
    val listState = rememberTransformingLazyColumnState()
    BwScreenScaffold(scrollState = listState) { contentPadding ->
        TransformingLazyColumn(state = listState, contentPadding = contentPadding) {
            edgeItem { BwHeader(stringResource(R.string.pair_title)) }
            when (state) {
                PairingState.Loading -> {
                    edgeItem { BwProgress() }
                    edgeItem { Step(stringResource(R.string.pair_loading)) }
                }
                // El código primero (es lo que hay que copiar); debajo, dónde escribirlo, con la
                // dirección del panel escrita: antes solo decía «el panel web».
                is PairingState.Code -> {
                    edgeItem { CodeBoxes(state.code) }
                    edgeItem { Step(stringResource(R.string.pair_step_open)) }
                    edgeItem {
                        BwText(
                            text = stringResource(R.string.panel_url),
                            style = BwTheme.type.label,
                            color = BwTheme.colors.info,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    edgeItem { Step(stringResource(R.string.pair_step_settings)) }
                    edgeItem { Step(stringResource(R.string.pair_waiting)) }
                }
                PairingState.Error -> {
                    edgeItem { BwText(stringResource(R.string.pair_error), textAlign = TextAlign.Center) }
                    edgeItem {
                        BwButton(
                            label = stringResource(R.string.pair_retry),
                            onClick = onRetry,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Step(text: String) {
    BwText(
        text = text,
        style = BwTheme.type.support,
        color = BwTheme.colors.contentSoft,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
}

/**
 * El código letra a letra, cada una en su casilla, para copiarlo sin errores. TalkBack lo lee
 * carácter a carácter. Los separadores del código (guion o espacio) quedan como hueco.
 */
@Composable
private fun CodeBoxes(code: String) {
    val colors = BwTheme.colors
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .semantics { contentDescription = code.filter(Char::isLetterOrDigit).toList().joinToString(" ") },
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs, Alignment.CenterHorizontally),
    ) {
        code.forEach { char ->
            if (char.isLetterOrDigit()) {
                Box(
                    modifier =
                        Modifier
                            .weight(1f, fill = false)
                            .widthIn(max = Sizes.codeBox)
                            .fillMaxWidth()
                            .heightIn(min = Sizes.codeBox * 1.4f)
                            .clip(BwTheme.shapes.card)
                            .background(colors.surface)
                            .clearAndSetSemantics {},
                    contentAlignment = Alignment.Center,
                ) {
                    BwText(char.toString(), style = BwTheme.type.title, textAlign = TextAlign.Center)
                }
            } else {
                Box(Modifier.widthIn(min = Spacing.sm))
            }
        }
    }
}
