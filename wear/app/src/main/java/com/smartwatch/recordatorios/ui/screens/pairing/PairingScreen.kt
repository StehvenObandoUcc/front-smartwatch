package com.smartwatch.recordatorios.ui.screens.pairing

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import com.smartwatch.recordatorios.R
import com.smartwatch.recordatorios.ui.components.BwButton
import com.smartwatch.recordatorios.ui.components.BwHeader
import com.smartwatch.recordatorios.ui.components.BwScreenScaffold
import com.smartwatch.recordatorios.ui.components.BwText
import com.smartwatch.recordatorios.ui.theme.BwTheme

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
            item { BwHeader(stringResource(R.string.pair_title)) }
            when (state) {
                PairingState.Loading ->
                    item { BwText(stringResource(R.string.pair_loading), textAlign = TextAlign.Center) }
                is PairingState.Code -> {
                    item {
                        BwText(
                            text = state.code,
                            style = BwTheme.type.code,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    item {
                        BwText(
                            text = stringResource(R.string.pair_instructions),
                            style = BwTheme.type.support,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
                PairingState.Error -> {
                    item { BwText(stringResource(R.string.pair_error), textAlign = TextAlign.Center) }
                    item {
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
