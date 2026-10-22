package com.smartwatch.recordatorios

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.smartwatch.recordatorios.alarm.DoseNotifier
import com.smartwatch.recordatorios.data.repository.DoseRepository
import com.smartwatch.recordatorios.di.ApplicationScope
import com.smartwatch.recordatorios.sync.SyncScheduler
import com.smartwatch.recordatorios.tile.requestDayTileUpdate
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class RecordatoriosApp :
    Application(),
    Configuration.Provider {
    @Inject lateinit var notifier: DoseNotifier

    @Inject lateinit var workerFactory: HiltWorkerFactory

    @Inject lateinit var syncScheduler: SyncScheduler

    @Inject lateinit var doses: DoseRepository

    @Inject @ApplicationScope
    lateinit var appScope: CoroutineScope

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

    override fun onCreate() {
        super.onCreate()
        notifier.ensureChannel()
        syncScheduler.schedulePeriodic()
        // Un solo punto para el Tile: cualquier cambio de dosis (registrar, posponer, sincronizar,
        // modo local) lo refresca, venga de la app, de la alarma o del worker.
        appScope.launch {
            doses.observeDoses().distinctUntilChanged().collect {
                requestDayTileUpdate(
                    this@RecordatoriosApp,
                )
            }
        }
    }
}
