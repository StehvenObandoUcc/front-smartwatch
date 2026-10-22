package com.smartwatch.recordatorios.ui.screens.watch

import androidx.compose.foundation.layout.Arrangement
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
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnScope
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import com.smartwatch.recordatorios.R
import com.smartwatch.recordatorios.data.local.LocalMedicationEntity
import com.smartwatch.recordatorios.ui.components.BwActionPill
import com.smartwatch.recordatorios.ui.components.BwBanner
import com.smartwatch.recordatorios.ui.components.BwButton
import com.smartwatch.recordatorios.ui.components.BwCard
import com.smartwatch.recordatorios.ui.components.BwHeader
import com.smartwatch.recordatorios.ui.components.BwScreenScaffold
import com.smartwatch.recordatorios.ui.components.BwText
import com.smartwatch.recordatorios.ui.components.MedicationLabel
import com.smartwatch.recordatorios.ui.components.edgeItem
import com.smartwatch.recordatorios.ui.theme.BwTheme
import com.smartwatch.recordatorios.ui.theme.Spacing

/** Textos de una confirmación; [name] rellena el título («¿Quitar Losartán?»). */
private data class Confirm(
    val title: Int,
    val body: Int,
    val yes: Int,
    val name: String? = null,
)

/**
 * Última página: cómo se usa el reloj (vinculado o solo aquí), los medicamentos creados en el reloj
 * (tocar uno para quitarlo) y Desvincular o Vincular. Lo que no se deshace pide confirmación con
 * Volver primero.
 */
@Composable
fun WatchScreen(
    paired: Boolean,
    medications: List<LocalMedicationEntity>,
    actions: WatchActions,
    patientName: String? = null,
) {
    // Qué se está confirmando: el id de un medicamento local, UNPAIR o nada.
    var pendingId by rememberSaveable { mutableStateOf<String?>(null) }
    val removing = medications.find { it.id == pendingId }
    val listState = rememberTransformingLazyColumnState()
    LaunchedEffect(pendingId) { listState.scrollToItem(0) }
    BwScreenScaffold(scrollState = listState) { contentPadding ->
        TransformingLazyColumn(
            state = listState,
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            when {
                pendingId == UNPAIR ->
                    confirmation(
                        Confirm(R.string.watch_unpair_title, R.string.watch_unpair_body, R.string.watch_unpair_yes),
                        onBack = { pendingId = null },
                        onYes = actions.onUnpair,
                    )
                removing != null ->
                    confirmation(
                        Confirm(
                            R.string.watch_remove_title,
                            R.string.watch_remove_body,
                            R.string.watch_remove_yes,
                            removing.name,
                        ),
                        onBack = { pendingId = null },
                        onYes = {
                            actions.onRemove(removing.id)
                            pendingId = null
                        },
                    )
                else -> overview(paired, medications, actions, patientName) { pendingId = it }
            }
        }
    }
}

private fun TransformingLazyColumnScope.overview(
    paired: Boolean,
    medications: List<LocalMedicationEntity>,
    actions: WatchActions,
    patientName: String?,
    onAsk: (String) -> Unit,
) {
    edgeItem { BwHeader(stringResource(R.string.watch_title), icon = R.drawable.ic_watch) }
    edgeItem {
        Soft(
            when {
                !paired -> stringResource(R.string.watch_local)
                patientName != null -> stringResource(R.string.watch_paired_with, patientName)
                else -> stringResource(R.string.watch_paired)
            },
        )
    }
    if (medications.isNotEmpty()) {
        edgeItem { Soft(stringResource(R.string.watch_local_meds)) }
        medications.forEach { medication ->
            edgeItem(key = medication.id, container = true) {
                BwCard(
                    onClick = { onAsk(medication.id) },
                    onClickLabel = stringResource(R.string.watch_remove),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    MedicationLabel(name = medication.name, colorKey = medication.colorKey)
                    BwText(
                        text = "${medication.doseLabel} · ${medication.times.replace(",", " · ")}",
                        style = BwTheme.type.support,
                        color = BwTheme.colors.contentSoft,
                    )
                }
            }
        }
    }
    actions.onAdd?.let { add ->
        edgeItem(container = true) {
            BwButton(
                label = stringResource(R.string.home_add),
                onClick = add,
                primary = medications.isEmpty(),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
    edgeItem(container = true) {
        if (paired) {
            BwActionPill(
                label = stringResource(R.string.watch_unpair),
                icon = R.drawable.ic_close,
                onClick = { onAsk(UNPAIR) },
                quiet = true,
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            BwButton(
                label = stringResource(R.string.watch_pair),
                onClick = actions.onPair,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

private fun TransformingLazyColumnScope.confirmation(
    confirm: Confirm,
    onBack: () -> Unit,
    onYes: () -> Unit,
) {
    edgeItem(container = true) {
        BwBanner(
            icon = R.drawable.ic_warning,
            iconDescription = stringResource(R.string.warning_label),
            title = confirm.name?.let { stringResource(confirm.title, it) } ?: stringResource(confirm.title),
            body = stringResource(confirm.body),
            modifier = Modifier.fillMaxWidth(),
        )
    }
    edgeItem {
        BwButton(
            label = stringResource(R.string.skip_confirm_no),
            onClick = onBack,
            primary = true,
            icon = R.drawable.ic_back,
            modifier = Modifier.fillMaxWidth(),
        )
    }
    edgeItem(container = true) {
        BwActionPill(
            label = stringResource(confirm.yes),
            icon = R.drawable.ic_close,
            onClick = onYes,
            quiet = true,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun Soft(text: String) {
    BwText(
        text = text,
        color = BwTheme.colors.contentSoft,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
}

private const val UNPAIR = "unpair"
