package com.smartwatch.recordatorios.data.repository

import com.smartwatch.recordatorios.data.local.DoseEntity
import java.time.Duration

/** Datos falsos del prototipo de alarma (fase 0). Se retiran cuando el plan llegue del backend. */
object DemoDoses {
    private val FIRST_OFFSET: Duration = Duration.ofMinutes(1)
    private val SECOND_OFFSET: Duration = Duration.ofMinutes(2)

    fun create(nowMillis: Long): List<DoseEntity> {
        val first = nowMillis + FIRST_OFFSET.toMillis()
        val second = nowMillis + SECOND_OFFSET.toMillis()
        return listOf(
            DoseEntity(
                id = "demo-1",
                medicationName = "Losartán",
                colorKey = "blue",
                doseLabel = "50 mg",
                scheduledAt = first,
                nextAlarmAt = first,
            ),
            DoseEntity(
                id = "demo-2",
                medicationName = "Metformina",
                colorKey = "orange",
                doseLabel = "850 mg",
                scheduledAt = second,
                nextAlarmAt = second,
            ),
        )
    }
}
