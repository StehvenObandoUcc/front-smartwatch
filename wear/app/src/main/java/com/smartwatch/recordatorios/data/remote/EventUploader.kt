package com.smartwatch.recordatorios.data.remote

import com.smartwatch.recordatorios.api.apis.DosesApi
import com.smartwatch.recordatorios.api.models.DoseEventBatch
import com.smartwatch.recordatorios.api.models.DoseEventInput
import com.smartwatch.recordatorios.api.models.DoseEventStatus
import com.smartwatch.recordatorios.data.local.AppDatabase
import com.smartwatch.recordatorios.data.local.DoseAction
import com.smartwatch.recordatorios.data.local.DoseEventEntity
import java.io.IOException
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/** Sube los eventos PENDING en lotes y los marca CONFIRMED (el backend es idempotente por eventId). */
@Singleton
class EventUploader
    @Inject
    constructor(
        private val api: DosesApi,
        db: AppDatabase,
    ) {
        private val eventDao = db.doseEventDao()

        /** true si no queda nada pendiente; false si hay que reintentar más tarde. */
        suspend fun upload(): Boolean {
            var batch = eventDao.pending(BATCH_SIZE)
            while (batch.isNotEmpty()) {
                if (!uploadBatch(batch)) return false
                batch = eventDao.pending(BATCH_SIZE)
            }
            return true
        }

        private suspend fun uploadBatch(batch: List<DoseEventEntity>): Boolean {
            val results =
                try {
                    api.createMyDoseEvents(DoseEventBatch(batch.map { it.toInput() })).body()?.results
                } catch (_: IOException) {
                    null
                } ?: return false
            // created, duplicate y rejected son respuestas definitivas del backend: no se reintentan.
            val answered = results.map { it.eventId.toString() }.toSet()
            val confirmed = batch.map { it.eventId }.filter { it in answered }
            if (confirmed.isNotEmpty()) eventDao.markConfirmed(confirmed)
            return confirmed.isNotEmpty()
        }

        private fun DoseEventEntity.toInput() =
            DoseEventInput(
                eventId = UUID.fromString(eventId),
                scheduleId = UUID.fromString(scheduleId),
                scheduledAt = utc(scheduledAt),
                status = if (action == DoseAction.TAKEN) DoseEventStatus.TAKEN else DoseEventStatus.SKIPPED,
                actedAt = utc(occurredAt),
            )

        private fun utc(epochMillis: Long): OffsetDateTime =
            OffsetDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneOffset.UTC)

        private companion object {
            const val BATCH_SIZE = 100
        }
    }
