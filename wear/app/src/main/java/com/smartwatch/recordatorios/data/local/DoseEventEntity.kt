package com.smartwatch.recordatorios.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class DoseAction { TAKEN, SNOOZED, SKIPPED }

enum class SyncState { PENDING, CONFIRMED }

/**
 * Acción del usuario sobre una dosis. `eventId` (UUID generado en el reloj) permite al backend
 * descartar duplicados; se sube en lote y pasa a CONFIRMED (fase 3).
 */
@Entity(
    tableName = "dose_events",
    indices = [Index("doseId"), Index("syncState")],
)
data class DoseEventEntity(
    @PrimaryKey val eventId: String,
    val doseId: String,
    val action: DoseAction,
    val occurredAt: Long,
    val syncState: SyncState = SyncState.PENDING,
)
