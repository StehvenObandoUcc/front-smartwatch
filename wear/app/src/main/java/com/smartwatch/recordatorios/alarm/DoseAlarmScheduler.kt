package com.smartwatch.recordatorios.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.smartwatch.recordatorios.MainActivity
import com.smartwatch.recordatorios.data.local.DoseEntity
import com.smartwatch.recordatorios.data.local.DoseStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Clock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DoseAlarmScheduler
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
        private val alarmManager: AlarmManager,
        private val clock: Clock,
    ) {
        fun canScheduleExactAlarms(): Boolean = alarmManager.canScheduleExactAlarms()

        /**
         * Programa la alarma de la dosis. Con permiso usa `setAlarmClock` (suena aunque el reloj
         * esté en reposo o Doze); sin permiso cae a una alarma inexacta y la UI guía al permiso.
         * Devuelve false si la dosis no debe sonar (resuelta o con hora pasada).
         */
        fun schedule(dose: DoseEntity): Boolean {
            val operation = alarmIntent(dose.id)
            if (dose.status != DoseStatus.SCHEDULED || dose.nextAlarmAt <= clock.millis()) {
                alarmManager.cancel(operation)
                return false
            }
            if (canScheduleExactAlarms()) {
                val info = AlarmManager.AlarmClockInfo(dose.nextAlarmAt, showIntent())
                alarmManager.setAlarmClock(info, operation)
            } else {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, dose.nextAlarmAt, operation)
            }
            return true
        }

        fun scheduleAll(doses: List<DoseEntity>): Int = doses.count { schedule(it) }

        fun cancel(doseId: String) {
            alarmManager.cancel(alarmIntent(doseId))
        }

        private fun alarmIntent(doseId: String): PendingIntent {
            val intent =
                Intent(context, DoseAlarmReceiver::class.java)
                    .setAction(DoseIntents.ACTION_ALARM)
                    .setData(DoseIntents.uriFor(doseId))
            return PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }

        // Lo que se abre al tocar el icono de alarma próxima del sistema.
        private fun showIntent(): PendingIntent =
            PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
    }
