package com.smartwatch.recordatorios.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class DoseAction { TAKEN, SNOOZED, SKIPPED }

/** UNDONE: «Deshacer» de una toma que hay que borrar en el backend (si llegó a subirse). */
enum class SyncState { PENDING, CONFIRMED, UNDONE }

/**
 * Acción del usuario sobre una dosis. `eventId` (UUID generado en el reloj) permite al backend
 * descartar duplicados; se sube en lote y pasa a CONFIRMED. Posponer es local: nace CONFIRMED.
 * Al deshacerla pasa a UNDONE hasta que el backend confirma el borrado.
 */
@Entity(
    tableName = "dose_events",
    indices = [Index("doseId"), Index("syncState")],
)
data class DoseEventEntity(
    @PrimaryKey val eventId: String,
    val doseId: String,
    val scheduleId: String,
    /** Hora prevista de la dosis (epoch UTC): junto a `scheduleId` identifica la dosis en el backend. */
    val scheduledAt: Long,
    val action: DoseAction,
    val occurredAt: Long,
    val syncState: SyncState = SyncState.PENDING,
)
