package com.smartwatch.recordatorios

import android.app.AlarmManager
import android.app.Application
import androidx.room.Room
import androidx.room.withTransaction
import androidx.test.core.app.ApplicationProvider
import com.smartwatch.recordatorios.alarm.AlarmRescheduler
import com.smartwatch.recordatorios.alarm.DoseActionHandler
import com.smartwatch.recordatorios.alarm.DoseAlarmFiring
import com.smartwatch.recordatorios.alarm.DoseAlarmScheduler
import com.smartwatch.recordatorios.alarm.DoseNotifier
import com.smartwatch.recordatorios.data.local.AppDatabase
import com.smartwatch.recordatorios.data.local.DoseEntity
import com.smartwatch.recordatorios.data.remote.EventUploader
import com.smartwatch.recordatorios.data.remote.PlanPrefs
import com.smartwatch.recordatorios.data.remote.PlanRepository
import com.smartwatch.recordatorios.data.repository.DemoDoses
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
    var uploadRequests = 0
    var fullScreenAllowed = true
    val notifier = DoseNotifier(context) { fullScreenAllowed }
    val handler = DoseActionHandler(repository, scheduler, notifier) { uploadRequests++ }
    val rescheduler = AlarmRescheduler(repository, scheduler)
    val firing = DoseAlarmFiring(repository, notifier)
    val planApi = FakePlanApi()
    val dosesApi = FakeDosesApi()
    val planPrefs = PlanPrefs(context)
    val planRepository = PlanRepository(planApi, db, scheduler, planPrefs, clock)
    val uploader = EventUploader(dosesApi, db)

    /** Reemplaza el plan por las dos dosis de ejemplo (en 1 y 2 minutos). */
    suspend fun seed(): List<DoseEntity> {
        val doses = DemoDoses.create(now)
        db.withTransaction {
            db.doseDao().deleteAll()
            db.doseDao().upsert(doses)
        }
        return doses
    }

    fun close() = db.close()

    companion object {
        /** 2026-01-15 12:00:00 UTC */
        const val T0 = 1_768_478_400_000L
        const val MINUTE = 60_000L
    }
}
