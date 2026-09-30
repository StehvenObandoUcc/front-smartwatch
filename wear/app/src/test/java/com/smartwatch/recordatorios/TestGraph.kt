package com.smartwatch.recordatorios

import android.app.AlarmManager
import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.smartwatch.recordatorios.alarm.AlarmRescheduler
import com.smartwatch.recordatorios.alarm.DoseActionHandler
import com.smartwatch.recordatorios.alarm.DoseAlarmFiring
import com.smartwatch.recordatorios.alarm.DoseAlarmScheduler
import com.smartwatch.recordatorios.alarm.DoseNotifier
import com.smartwatch.recordatorios.data.local.AppDatabase
import com.smartwatch.recordatorios.data.repository.DoseRepository
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowAlarmManager
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

/** Grafo de dependencias para pruebas, con reloj controlable y Room en memoria. */
class TestGraph(
    var now: Long = T0,
) {
    val context: Application = ApplicationProvider.getApplicationContext()

    private val clock =
        object : Clock() {
            override fun getZone() = ZoneOffset.UTC

            override fun withZone(zone: java.time.ZoneId?) = this

            override fun instant(): Instant = Instant.ofEpochMilli(now)
        }

    val db: AppDatabase =
        Room
            .inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

    val alarmManager: AlarmManager = context.getSystemService(AlarmManager::class.java)
    val shadowAlarms: ShadowAlarmManager = shadowOf(alarmManager)

    val repository = DoseRepository(db, clock)
    val scheduler = DoseAlarmScheduler(context, alarmManager, clock)
    val notifier = DoseNotifier(context)
    val handler = DoseActionHandler(repository, scheduler, notifier)
    val rescheduler = AlarmRescheduler(repository, scheduler)
    val firing = DoseAlarmFiring(repository, notifier)

    fun close() = db.close()

    companion object {
        /** 2026-01-15 12:00:00 UTC */
        const val T0 = 1_768_478_400_000L
        const val MINUTE = 60_000L
    }
}
