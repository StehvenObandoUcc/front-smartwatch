package com.smartwatch.recordatorios.alarm

import com.smartwatch.recordatorios.data.local.DoseStatus
import com.smartwatch.recordatorios.data.repository.DoseRepository
import javax.inject.Inject
import javax.inject.Singleton

/** Qué pasa cuando vence la alarma de una dosis. */
@Singleton
class DoseAlarmFiring
    @Inject
    constructor(
        private val repository: DoseRepository,
        private val notifier: DoseNotifier,
    ) {
        /** Muestra la alerta si la dosis sigue pendiente. Devuelve true si se mostró. */
        suspend fun fire(doseId: String): Boolean {
            val dose = repository.get(doseId)
            if (dose == null || dose.status != DoseStatus.SCHEDULED) return false
            notifier.show(dose)
            return true
        }
    }
