package com.smartwatch.recordatorios.ui.screens.watch

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import com.smartwatch.recordatorios.R
import com.smartwatch.recordatorios.ui.components.BwActionPill
import com.smartwatch.recordatorios.ui.components.BwBanner
import com.smartwatch.recordatorios.ui.components.BwButton
import com.smartwatch.recordatorios.ui.components.BwHeader
import com.smartwatch.recordatorios.ui.components.BwScreenScaffold
import com.smartwatch.recordatorios.ui.components.BwText
import com.smartwatch.recordatorios.ui.components.edgeItem
import com.smartwatch.recordatorios.ui.theme.BwTheme

/**
 * Última página: el estado del reloj y Desvincular. Desvincular no se deshace, así que pide
 * confirmación con la opción segura (Volver) primero, como Omitir.
 */
@Composable
fun WatchScreen(onUnpair: () -> Unit) {
    var confirming by rememberSaveable { mutableStateOf(false) }
    val listState = rememberTransformingLazyColumnState()
    LaunchedEffect(confirming) { listState.scrollToItem(0) }
    BwScreenScaffold(scrollState = listState) { contentPadding ->
        TransformingLazyColumn(state = listState, contentPadding = contentPadding) {
            if (confirming) {
                edgeItem(container = true) {
                    BwBanner(
                        icon = R.drawable.ic_warning,
                        iconDescription = stringResource(R.string.warning_label),
                        title = stringResource(R.string.watch_unpair_title),
                        body = stringResource(R.string.watch_unpair_body),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                edgeItem {
                    BwButton(
                        label = stringResource(R.string.skip_confirm_no),
                        onClick = { confirming = false },
                        primary = true,
                        icon = R.drawable.ic_back,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                edgeItem(container = true) {
                    BwActionPill(
                        label = stringResource(R.string.watch_unpair_yes),
                        icon = R.drawable.ic_close,
                        onClick = onUnpair,
                        quiet = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            } else {
                edgeItem { BwHeader(stringResource(R.string.watch_title)) }
                edgeItem {
                    BwText(
                        text = stringResource(R.string.watch_paired),
                        color = BwTheme.colors.contentSoft,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                edgeItem(container = true) {
                    BwActionPill(
                        label = stringResource(R.string.watch_unpair),
                        icon = R.drawable.ic_close,
                        onClick = { confirming = true },
                        quiet = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}
