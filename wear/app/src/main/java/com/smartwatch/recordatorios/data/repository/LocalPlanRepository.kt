package com.smartwatch.recordatorios.data.repository

import androidx.room.withTransaction
import com.smartwatch.recordatorios.alarm.DoseAlarmScheduler
import com.smartwatch.recordatorios.data.local.AppDatabase
import com.smartwatch.recordatorios.data.local.DoseEntity
import com.smartwatch.recordatorios.data.local.DoseStatus
import com.smartwatch.recordatorios.data.local.LocalMedicationEntity
import kotlinx.coroutines.flow.Flow
import java.time.Clock
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/** Prefijo del `scheduleId` de las dosis creadas en el reloj: no son del backend. */
const val LOCAL_PREFIX = "local:"

fun isLocal(scheduleId: String) = scheduleId.startsWith(LOCAL_PREFIX)

/**
 * Modo sin vincular: medicamentos creados en el reloj, con dosis diarias generadas para
 * [DAYS_AHEAD] días y sus alarmas, como si fuera un plan. [refill] es idempotente y se llama al
 * crear, al quitar, al abrir la app y en cada sincronización periódica, para que siempre haya plan.
 */
@Singleton
class LocalPlanRepository
    @Inject
    constructor(
        private val db: AppDatabase,
        private val scheduler: DoseAlarmScheduler,
        private val clock: Clock,
    ) {
        private val medicationDao = db.localMedicationDao()
        private val doseDao = db.doseDao()

        val medications: Flow<List<LocalMedicationEntity>> = medicationDao.observeAll()

        suspend fun add(
            name: String,
            doseLabel: String,
            times: List<LocalTime>,
        ): LocalMedicationEntity {
            // El color va siempre con el nombre; se reparte en orden para distinguir los de la lista.
            val color = PALETTE[medicationDao.all().size % PALETTE.size]
            val medication =
                LocalMedicationEntity(
                    id = UUID.randomUUID().toString(),
                    name = name.trim(),
                    doseLabel = doseLabel.trim(),
                    colorKey = color,
                    times = times.distinct().sorted().joinToString(",") { it.format(TIME) },
                )
            medicationDao.upsert(medication)
            refill()
            return medication
        }

        /** Quita el medicamento y sus dosis pendientes (con sus alarmas); lo ya tomado se conserva. */
        suspend fun remove(id: String) {
            val pending =
                db.withTransaction {
                    medicationDao.delete(id)
                    doseDao.forSchedule(LOCAL_PREFIX + id).filter { it.status == DoseStatus.SCHEDULED }.also {
                        doseDao.delete(it.map { dose -> dose.id })
                    }
                }
            pending.forEach { scheduler.cancel(it.id) }
        }

        /** Crea las dosis futuras que falten hasta [DAYS_AHEAD] días y programa sus alarmas. */
        suspend fun refill() {
            val now = clock.millis()
            val zone = ZoneId.systemDefault()
            val today = LocalDate.now(clock.withZone(zone))
            val created =
                db.withTransaction {
                    val existing = doseDao.all().mapTo(HashSet()) { it.id }
                    val fresh =
                        medicationDao
                            .all()
                            .flatMap { medication ->
                                (0 until DAYS_AHEAD).flatMap { offset ->
                                    medication.timeList().map { time ->
                                        val at =
                                            today
                                                .plusDays(
                                                    offset.toLong(),
                                                ).atTime(time)
                                                .atZone(zone)
                                                .toInstant()
                                                .toEpochMilli()
                                        medication.dose(at)
                                    }
                                }
                            }.filter { it.scheduledAt > now && it.id !in existing }
                    doseDao.upsert(fresh)
                    fresh
                }
            scheduler.scheduleAll(created)
        }

        private fun LocalMedicationEntity.timeList(): List<LocalTime> =
            times.split(",").filter { it.isNotBlank() }.map { LocalTime.parse(it, TIME) }

        private fun LocalMedicationEntity.dose(at: Long) =
            DoseEntity(
                id = "$LOCAL_PREFIX$id@$at",
                scheduleId = LOCAL_PREFIX + id,
                medicationName = name,
                colorKey = colorKey,
                doseLabel = doseLabel,
                scheduledAt = at,
                nextAlarmAt = at,
            )

        private companion object {
            const val DAYS_AHEAD = 7
            val TIME: java.time.format.DateTimeFormatter =
                java.time.format.DateTimeFormatter
                    .ofPattern("HH:mm")
            val PALETTE = listOf("blue", "orange", "green", "pink", "teal", "purple", "red", "yellow")
        }
    }
