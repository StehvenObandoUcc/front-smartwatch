package com.smartwatch.recordatorios.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class DoseStatus { SCHEDULED, TAKEN, SKIPPED }

/**
 * Una dosis del plan. Las horas son epoch en milisegundos UTC; se formatean con la zona del reloj.
 */
@Entity(tableName = "doses")
data class DoseEntity(
    @PrimaryKey val id: String,
    val scheduleId: String,
    val medicationName: String,
    /** Clave de la paleta de medicamentos; en pantalla siempre va junto al nombre. */
    val colorKey: String,
    /** Dosis por toma ("1 tableta"). */
    val doseLabel: String,
    /** Hora prevista de la toma. */
    val scheduledAt: Long,
    /** Próxima vez que debe sonar (cambia al posponer). */
    val nextAlarmAt: Long,
    val snoozeCount: Int = 0,
    val status: DoseStatus = DoseStatus.SCHEDULED,
)
