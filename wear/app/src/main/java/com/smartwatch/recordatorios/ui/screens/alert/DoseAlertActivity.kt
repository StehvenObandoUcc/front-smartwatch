package com.smartwatch.recordatorios.ui.screens.alert

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.smartwatch.recordatorios.alarm.DoseActionHandler
import com.smartwatch.recordatorios.alarm.DoseIntents
import com.smartwatch.recordatorios.data.local.DoseAction
import com.smartwatch.recordatorios.data.local.DoseEntity
import com.smartwatch.recordatorios.data.local.DoseStatus
import com.smartwatch.recordatorios.data.repository.DoseRepository
import com.smartwatch.recordatorios.ui.components.BwAppScaffold
import com.smartwatch.recordatorios.ui.screens.home.todayDoses
import com.smartwatch.recordatorios.ui.theme.RecordatoriosTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

/**
 * Se abre desde el full-screen intent de la notificación; enciende la pantalla y se muestra
 * sobre la pantalla de bloqueo (atributos en el manifiesto).
 */
@AndroidEntryPoint
class DoseAlertActivity : ComponentActivity() {
    @Inject lateinit var repository: DoseRepository

    @Inject lateinit var handler: DoseActionHandler

    private var dose by mutableStateOf<DoseEntity?>(null)

    /** Las demás dosis de hoy ya están registradas: tomar esta cierra el día. */
    private var lastOfDay by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecordatoriosTheme {
                BwAppScaffold {
                    dose?.let { current ->
                        DoseAlertScreen(
                            dose = current,
                            onTake = { act(current.id, DoseAction.TAKEN, closeAfterMillis = UNDO_MILLIS) },
                            onSnooze = { act(current.id, DoseAction.SNOOZED, closeAfterMillis = SUCCESS_MILLIS) },
                            onSkip = { act(current.id, DoseAction.SKIPPED) },
                            onUndo = { undo(current.id) },
                            lastOfDay = lastOfDay,
                        )
                    }
                }
            }
        }
        load()
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        load()
    }

    private fun load() {
        val doseId = DoseIntents.doseId(intent) ?: return finish()
        lifecycleScope.launch {
            dose = repository.get(doseId) ?: return@launch finish()
            val zone = ZoneId.systemDefault()
            lastOfDay =
                todayDoses(repository.observeDoses().first(), LocalDate.now(zone), zone)
                    .filter { it.id != doseId }
                    .none { it.status == DoseStatus.SCHEDULED }
        }
    }

    private var closing: Job? = null

    /**
     * Registra la acción al momento; tras «Tomada» o «Posponer» deja ver la confirmación antes de
     * cerrar (tras «Tomada», el tiempo de «Deshacer»).
     */
    private fun act(
        doseId: String,
        action: DoseAction,
        closeAfterMillis: Long = 0,
    ) {
        closing =
            lifecycleScope.launch {
                // El registro no se corta aunque «Deshacer» cancele la espera para cerrar.
                withContext(NonCancellable) { handler.handle(doseId, action) }
                delay(closeAfterMillis)
                finish()
            }
    }

    /** «Deshacer»: no se cierra; la dosis vuelve a pendiente y la alerta muestra otra vez sus acciones. */
    private fun undo(doseId: String) {
        closing?.cancel()
        lifecycleScope.launch {
            closing?.join()
            if (handler.undoTaken(doseId)) {
                dose = repository.get(doseId)
            } else {
                finish()
            }
        }
    }

    private companion object {
        const val SUCCESS_MILLIS = 1_500L
        const val UNDO_MILLIS = 5_000L
    }
}
