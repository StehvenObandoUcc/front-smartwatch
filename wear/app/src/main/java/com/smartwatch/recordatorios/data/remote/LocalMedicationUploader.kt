package com.smartwatch.recordatorios.data.remote

import com.smartwatch.recordatorios.api.apis.MedicationsApi
import com.smartwatch.recordatorios.api.models.DeviceMedicationCreate
import com.smartwatch.recordatorios.api.models.ScheduleCreate
import com.smartwatch.recordatorios.data.local.AppDatabase
import com.smartwatch.recordatorios.data.local.LocalMedicationEntity
import com.smartwatch.recordatorios.data.repository.LocalPlanRepository
import java.io.IOException
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Tras vincular, sube los medicamentos creados en el reloj (`POST /devices/me/medications`) para que
 * el cuidador los vea en la web. El id local es el `clientId`: el backend no los duplica si se
 * reintenta. Al confirmarse se quitan del plan local y vuelven en el plan del backend.
 */
@Singleton
class LocalMedicationUploader
    @Inject
    constructor(
        private val api: MedicationsApi,
        db: AppDatabase,
        private val localPlan: LocalPlanRepository,
        private val clock: Clock,
    ) {
        private val medicationDao = db.localMedicationDao()

        /** true si no queda nada por reintentar (subido o rechazado por el backend); false sin red. */
        suspend fun upload(): Boolean {
            for (medication in medicationDao.all()) {
                val response =
                    try {
                        api.createMyMedication(medication.toRequest())
                    } catch (_: IOException) {
                        return false
                    }
                // 201 creado o 200 ya existía: es del backend. Otro código (p. ej. sin consentimiento)
                // lo deja en el reloj, que lo sigue haciendo sonar.
                if (response.isSuccessful) localPlan.remove(medication.id)
            }
            return true
        }

        private fun LocalMedicationEntity.toRequest() =
            DeviceMedicationCreate(
                clientId = UUID.fromString(id),
                name = name,
                dosage = doseLabel,
                color = WEB_COLORS[colorKey],
                schedule =
                    ScheduleCreate(
                        times = times.split(",").filter { it.isNotBlank() }.toSet(),
                        daysOfWeek = EVERY_DAY,
                        startDate = LocalDate.now(clock.withZone(ZoneId.systemDefault())),
                    ),
            )

        private companion object {
            val EVERY_DAY = (1..7).toSet()
            val WEB_COLORS = COLOR_KEYS.entries.associate { (hex, key) -> key to hex }
        }
    }
