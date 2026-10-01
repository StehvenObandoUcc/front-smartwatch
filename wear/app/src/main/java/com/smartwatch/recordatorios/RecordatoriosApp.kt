package com.smartwatch.recordatorios

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.smartwatch.recordatorios.alarm.DoseNotifier
import com.smartwatch.recordatorios.sync.SyncScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class RecordatoriosApp :
    Application(),
    Configuration.Provider {
    @Inject lateinit var notifier: DoseNotifier

    @Inject lateinit var workerFactory: HiltWorkerFactory

    @Inject lateinit var syncScheduler: SyncScheduler

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

    override fun onCreate() {
        super.onCreate()
        notifier.ensureChannel()
        syncScheduler.schedulePeriodic()
    }
}
