package com.smartwatch.recordatorios.alarm

import com.smartwatch.recordatorios.data.local.DoseAction
import com.smartwatch.recordatorios.data.local.DoseEntity
import com.smartwatch.recordatorios.data.repository.DoseRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Punto único para Tomada / Posponer / Omitir, lo use la notificación o la pantalla de alerta.
 * Guarda el evento (PENDING) y ajusta alarma y notificación.
 */
@Singleton
class DoseActionHandler
    @Inject
    constructor(
        private val repository: DoseRepository,
        private val scheduler: DoseAlarmScheduler,
        private val notifier: DoseNotifier,
    ) {
        /** Devuelve la dosis actualizada, o null si la acción no aplicaba. */
        suspend fun handle(
            doseId: String,
            action: DoseAction,
        ): DoseEntity? {
            val updated = repository.record(doseId, action)
            notifier.cancel(doseId)
            when {
                updated == null -> Unit
                action == DoseAction.SNOOZED -> scheduler.schedule(updated)
                else -> scheduler.cancel(doseId)
            }
            return updated
        }
    }

/** Reprograma todas las dosis pendientes (arranque, actualización, permiso concedido). */
@Singleton
class AlarmRescheduler
    @Inject
    constructor(
        private val repository: DoseRepository,
        private val scheduler: DoseAlarmScheduler,
    ) {
        suspend fun rescheduleAll(): Int = scheduler.scheduleAll(repository.scheduledDoses())
    }
