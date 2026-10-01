package com.smartwatch.recordatorios.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.smartwatch.recordatorios.data.remote.EventUploader
import com.smartwatch.recordatorios.data.remote.PlanRepository
import com.smartwatch.recordatorios.data.remote.SyncResult
import com.smartwatch.recordatorios.data.remote.TokenStore
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/** Cada 30 min y al abrir la app: sube lo pendiente y descarga el plan (ETag evita descargas inútiles). */
@HiltWorker
class PlanSyncWorker
    @AssistedInject
    constructor(
        @Assisted context: Context,
        @Assisted params: WorkerParameters,
        private val uploader: EventUploader,
        private val plan: PlanRepository,
        private val tokens: TokenStore,
    ) : CoroutineWorker(context, params) {
        override suspend fun doWork(): Result {
            if (!tokens.paired.value) return Result.success()
            val uploaded = uploader.upload()
            val synced = plan.sync()
            val retry = !uploaded || synced == SyncResult.FAILED
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
