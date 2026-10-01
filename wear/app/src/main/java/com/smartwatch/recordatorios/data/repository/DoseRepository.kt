package com.smartwatch.recordatorios.data.repository

import androidx.room.withTransaction
import com.smartwatch.recordatorios.alarm.SnoozePolicy
import com.smartwatch.recordatorios.data.local.AppDatabase
import com.smartwatch.recordatorios.data.local.DoseAction
import com.smartwatch.recordatorios.data.local.DoseEntity
import com.smartwatch.recordatorios.data.local.DoseEventEntity
import com.smartwatch.recordatorios.data.local.DoseStatus
import com.smartwatch.recordatorios.data.local.SyncState
import kotlinx.coroutines.flow.Flow
import java.time.Clock
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DoseRepository
    @Inject
    constructor(
        private val db: AppDatabase,
        private val clock: Clock,
    ) {
        private val doseDao = db.doseDao()
        private val eventDao = db.doseEventDao()

        fun observeDoses(): Flow<List<DoseEntity>> = doseDao.observeAll()

        suspend fun get(id: String): DoseEntity? = doseDao.get(id)

        suspend fun scheduledDoses(): List<DoseEntity> = doseDao.scheduled()

        suspend fun eventsFor(doseId: String): List<DoseEventEntity> = eventDao.forDose(doseId)

        /**
         * Registra la acción y actualiza la dosis en una transacción.
         * Devuelve la dosis actualizada, o null si la acción no aplica (dosis ya resuelta,
         * límite de posposiciones alcanzado o dosis inexistente): así un doble toque no duplica.
         */
        suspend fun record(
            doseId: String,
            action: DoseAction,
        ): DoseEntity? =
            db.withTransaction {
                val dose = doseDao.get(doseId) ?: return@withTransaction null
                val now = clock.millis()
                val updated =
                    when {
                        dose.status != DoseStatus.SCHEDULED -> null
                        action == DoseAction.TAKEN -> dose.copy(status = DoseStatus.TAKEN)
                        action == DoseAction.SKIPPED -> dose.copy(status = DoseStatus.SKIPPED)
                        SnoozePolicy.canSnooze(dose.snoozeCount) ->
                            dose.copy(
                                snoozeCount = dose.snoozeCount + 1,
                                nextAlarmAt = SnoozePolicy.nextAlarmAt(now),
                            )
                        else -> null
                    }
                if (updated != null) {
                    doseDao.update(updated)
                    eventDao.insert(
                        DoseEventEntity(
                            eventId = UUID.randomUUID().toString(),
                            doseId = doseId,
                            scheduleId = dose.scheduleId,
                            scheduledAt = dose.scheduledAt,
                            action = action,
                            occurredAt = now,
                            syncState = if (action == DoseAction.SNOOZED) SyncState.CONFIRMED else SyncState.PENDING,
                        ),
                    )
                }
                updated
            }
    }
