package com.smartwatch.recordatorios.ui.screens.alert

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import androidx.wear.compose.material3.AppScaffold
import com.smartwatch.recordatorios.alarm.DoseActionHandler
import com.smartwatch.recordatorios.alarm.DoseIntents
import com.smartwatch.recordatorios.data.local.DoseAction
import com.smartwatch.recordatorios.data.local.DoseEntity
import com.smartwatch.recordatorios.data.repository.DoseRepository
import com.smartwatch.recordatorios.ui.theme.RecordatoriosTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecordatoriosTheme {
                AppScaffold {
                    dose?.let { current ->
                        DoseAlertScreen(
                            dose = current,
                            onTake = { act(current.id, DoseAction.TAKEN) },
                            onSnooze = { act(current.id, DoseAction.SNOOZED) },
                            onSkip = { act(current.id, DoseAction.SKIPPED) },
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
        }
    }

    private fun act(
        doseId: String,
        action: DoseAction,
    ) {
        lifecycleScope.launch {
            handler.handle(doseId, action)
            finish()
        }
    }
}
