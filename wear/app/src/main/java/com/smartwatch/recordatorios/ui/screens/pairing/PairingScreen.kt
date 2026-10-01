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
import androidx.wear.compose.material3.FilledTonalButton
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import com.smartwatch.recordatorios.R

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
    ScreenScaffold(scrollState = listState) { contentPadding ->
        TransformingLazyColumn(state = listState, contentPadding = contentPadding) {
            item { ListHeader { Text(stringResource(R.string.pair_title)) } }
            when (state) {
                PairingState.Loading ->
                    item { Text(stringResource(R.string.pair_loading), textAlign = TextAlign.Center) }
                is PairingState.Code -> {
                    item {
                        Text(
                            text = state.code,
                            style = MaterialTheme.typography.displaySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    item {
                        Text(
                            text = stringResource(R.string.pair_instructions),
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
                PairingState.Error -> {
                    item { Text(stringResource(R.string.pair_error), textAlign = TextAlign.Center) }
                    item {
                        FilledTonalButton(
                            onClick = onRetry,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.pair_retry)) },
                        )
                    }
                }
            }
        }
    }
}
