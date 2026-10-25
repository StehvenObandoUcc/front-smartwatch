package com.smartwatch.recordatorios.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.smartwatch.recordatorios.data.remote.AdherenceRepository
import com.smartwatch.recordatorios.data.remote.DeviceSession
import com.smartwatch.recordatorios.data.remote.EventUploader
import com.smartwatch.recordatorios.data.remote.LocalMedicationUploader
import com.smartwatch.recordatorios.data.remote.PlanRepository
import com.smartwatch.recordatorios.data.remote.SyncResult
import com.smartwatch.recordatorios.data.remote.TokenStore
import com.smartwatch.recordatorios.data.repository.LocalPlanRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Cada 15 min y al abrir la app: completa las dosis de los medicamentos creados en el reloj (también
 * sin vincular), sube lo pendiente (y borra las tomas deshechas), descarga el plan (ETag evita
 * descargas inútiles) y después la adherencia, que así ya cuenta lo recién subido. Al final informa
 * la batería.
 */
@Suppress("LongParameterList") // context y params los exige WorkManager
@HiltWorker
class PlanSyncWorker
    @AssistedInject
    constructor(
        @Assisted context: Context,
        @Assisted params: WorkerParameters,
        private val uploader: EventUploader,
        private val plan: PlanRepository,
        private val adherence: AdherenceRepository,
        private val tokens: TokenStore,
        private val localPlan: LocalPlanRepository,
        private val localUploader: LocalMedicationUploader,
        private val device: DeviceSession,
    ) : CoroutineWorker(context, params) {
        override suspend fun doWork(): Result {
            localPlan.refill()
            if (!tokens.paired.value) return Result.success()
            val uploaded = uploader.upload()
            // Antes del plan: así lo creado en el reloj vuelve ya en esta misma descarga.
            val localUploaded = localUploader.upload()
            val synced = plan.sync()
            val adherenceSynced = adherence.sync()
            device.refresh()
            device.reportBattery()
            val retry = !uploaded || !localUploaded || synced == SyncResult.FAILED || !adherenceSynced
            return if (retry) Result.retry() else Result.success()
        }
    }

/** Sube los eventos justo después de una acción; con reintentos y backoff si no hay red. */
@HiltWorker
class EventUploadWorker
    @AssistedInject
    constructor(
        @Assisted context: Context,
        @Assisted params: WorkerParameters,
        private val uploader: EventUploader,
        private val tokens: TokenStore,
    ) : CoroutineWorker(context, params) {
        override suspend fun doWork(): Result =
            if (!tokens.paired.value || uploader.upload()) Result.success() else Result.retry()
    }
