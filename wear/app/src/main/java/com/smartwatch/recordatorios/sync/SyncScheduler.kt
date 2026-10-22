package com.smartwatch.recordatorios.sync

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import javax.inject.Inject
import javax.inject.Singleton

/** Pide subir los eventos pendientes (lo usa DoseActionHandler; en pruebas se sustituye). */
fun interface UploadTrigger {
    fun request()
}

/** Pide descargar el plan ya (p. ej. tras crear un medicamento por el asistente). */
fun interface PlanRefresher {
    fun refresh()
}

@Singleton
class SyncScheduler
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
    ) : UploadTrigger,
        PlanRefresher {
        private val workManager get() = WorkManager.getInstance(context)
        private val needsNetwork = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        /** Respaldo periódico del plan. 15 min (el mínimo de WorkManager): sin FCM, es el único aviso de plan nuevo. */
        fun schedulePeriodic() {
            val request =
                PeriodicWorkRequestBuilder<PlanSyncWorker>(PERIOD)
                    .setConstraints(needsNetwork)
                    .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, BACKOFF)
                    .build()
            workManager.enqueueUniquePeriodicWork(PERIODIC, ExistingPeriodicWorkPolicy.KEEP, request)
        }

        /**
         * Al abrir la app y cada pocos minutos mientras está abierta: sincroniza ahora. REPLACE y no KEEP:
         * una sincronización fallida espera su backoff en cola y KEEP la dejaría ahí, retrasando el plan.
         */
        fun syncNow() {
            val request =
                OneTimeWorkRequestBuilder<PlanSyncWorker>()
                    .setConstraints(needsNetwork)
                    .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, BACKOFF)
                    .build()
            workManager.enqueueUniqueWork(NOW, ExistingWorkPolicy.REPLACE, request)
        }

        override fun refresh() = syncNow()

        override fun request() {
            val request =
                OneTimeWorkRequestBuilder<EventUploadWorker>()
                    .setConstraints(needsNetwork)
                    .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, BACKOFF)
                    .build()
            workManager.enqueueUniqueWork(UPLOAD, ExistingWorkPolicy.APPEND_OR_REPLACE, request)
        }

        private companion object {
            const val PERIODIC = "plan-sync-periodic"
            const val NOW = "plan-sync-now"
            const val UPLOAD = "event-upload"
            val PERIOD: Duration = Duration.ofMinutes(15)
            val BACKOFF: Duration = Duration.ofSeconds(30)
        }
    }
