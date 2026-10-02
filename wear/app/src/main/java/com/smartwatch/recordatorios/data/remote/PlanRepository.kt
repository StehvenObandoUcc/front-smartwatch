package com.smartwatch.recordatorios.data.remote

import androidx.room.withTransaction
import com.smartwatch.recordatorios.alarm.DoseAlarmScheduler
import com.smartwatch.recordatorios.api.apis.PlanApi
import com.smartwatch.recordatorios.api.models.Plan
import com.smartwatch.recordatorios.api.models.PlannedDose
import com.smartwatch.recordatorios.data.local.AppDatabase
import com.smartwatch.recordatorios.data.local.DoseEntity
import com.smartwatch.recordatorios.data.local.DoseStatus
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import retrofit2.Response
import java.time.Clock
import java.time.Duration
import javax.inject.Inject
import javax.inject.Singleton

enum class SyncResult { UPDATED, UNCHANGED, CONSENT_REQUIRED, UNAUTHORIZED, FAILED }

/** Descarga el plan de 7 días (con ETag) y lo deja en Room con las alarmas reprogramadas. */
@Singleton
class PlanRepository
    @Inject
    constructor(
        private val api: PlanApi,
        private val db: AppDatabase,
        private val scheduler: DoseAlarmScheduler,
        private val prefs: PlanPrefs,
        private val clock: Clock,
    ) {
        private val doseDao = db.doseDao()

        suspend fun sync(): SyncResult {
            val response =
                try {
                    api.getMyPlan(prefs.etag)
                } catch (_: java.io.IOException) {
                    return SyncResult.FAILED
                }
            return when {
                response.code() == HTTP_NOT_MODIFIED -> SyncResult.UNCHANGED
                response.isSuccessful -> applyBody(response)
                response.code() == HTTP_UNAUTHORIZED -> SyncResult.UNAUTHORIZED
                response.code() == HTTP_FORBIDDEN && response.problemCode() == CONSENT_REQUIRED -> {
                    prefs.update(PlanStatus.CONSENT_REQUIRED)
                    SyncResult.CONSENT_REQUIRED
                }
                else -> {
                    prefs.update(PlanStatus.FAILED)
                    SyncResult.FAILED
                }
            }
        }

        private suspend fun applyBody(response: Response<Plan>): SyncResult {
            val plan = response.body() ?: return SyncResult.FAILED
            apply(plan)
            prefs.etag = response.headers()["ETag"]
            prefs.update(PlanStatus.OK, plan.validUntil.toInstant().toEpochMilli())
            return SyncResult.UPDATED
        }

        /**
         * Sustituye las dosis pendientes por las del plan, conservando el estado local (tomada,
         * omitida, posposiciones) de las que ya existían, y reprograma las alarmas.
         */
        suspend fun apply(plan: Plan) {
            val now = clock.millis()
            val incoming = plan.doses.map { it.toEntity() }.associateBy { it.id }
            val (merged, removed) =
                db.withTransaction {
                    val local = doseDao.all().associateBy { it.id }
                    val gone = local.values.filter { it.status == DoseStatus.SCHEDULED && it.id !in incoming }
                    doseDao.delete(gone.map { it.id })
                    val merged = incoming.values.map { fresh -> local[fresh.id]?.keepLocalState(fresh) ?: fresh }
                    doseDao.upsert(merged)
                    doseDao.deleteResolvedBefore(now - RETENTION.toMillis())
                    merged to gone
                }
            removed.forEach { scheduler.cancel(it.id) }
            scheduler.scheduleAll(merged)
        }

        /** Al vincular otro paciente: descarta el plan anterior y sus alarmas. */
        suspend fun reset() {
            doseDao.all().forEach { scheduler.cancel(it.id) }
            doseDao.deleteAll()
            prefs.clear()
        }

        private fun PlannedDose.toEntity(): DoseEntity {
            val at = scheduledAt.toInstant().toEpochMilli()
            return DoseEntity(
                id = "$scheduleId@$at",
                scheduleId = scheduleId.toString(),
                medicationName = medicationName,
                // Un color fuera de la paleta se guarda tal cual (#RRGGBB) y la UI lo pinta directo.
                colorKey = COLOR_KEYS[color.lowercase()] ?: color,
                doseLabel = dosage,
                scheduledAt = at,
                nextAlarmAt = at,
            )
        }

        private fun DoseEntity.keepLocalState(fresh: DoseEntity) =
            fresh.copy(status = status, snoozeCount = snoozeCount, nextAlarmAt = nextAlarmAt)

        private fun <T> Response<T>.problemCode(): String? =
            runCatching {
                Json
                    .parseToJsonElement(errorBody()?.string().orEmpty())
                    .jsonObject["code"]
                    ?.jsonPrimitive
                    ?.contentOrNull
            }.getOrNull()

        private companion object {
            const val HTTP_NOT_MODIFIED = 304
            const val HTTP_UNAUTHORIZED = 401
            const val HTTP_FORBIDDEN = 403
            const val CONSENT_REQUIRED = "consent_required"
            val RETENTION: Duration = Duration.ofDays(2)

            // Hex que envía la web (paleta de tokens) -> clave de la paleta del reloj.
            val COLOR_KEYS =
                mapOf(
                    "#c62828" to "red",
                    "#d4661a" to "orange",
                    "#b08900" to "yellow",
                    "#2e7d32" to "green",
                    "#00796b" to "teal",
                    "#1565c0" to "blue",
                    "#6a1b9a" to "purple",
                    "#ad1457" to "pink",
                )
        }
    }
