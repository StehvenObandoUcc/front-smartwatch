package com.smartwatch.recordatorios

import android.app.Application
import com.smartwatch.recordatorios.alarm.DoseNotifier
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class RecordatoriosApp : Application() {
    @Inject lateinit var notifier: DoseNotifier

    override fun onCreate() {
        super.onCreate()
        notifier.ensureChannel()
    }
}
