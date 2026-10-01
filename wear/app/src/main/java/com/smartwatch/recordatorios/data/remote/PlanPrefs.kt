package com.smartwatch.recordatorios.data.remote

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/** Resultado de la última descarga del plan, para mostrarlo en Inicio. */
enum class PlanStatus { NEVER, OK, CONSENT_REQUIRED, FAILED }

data class PlanInfo(
    val status: PlanStatus = PlanStatus.NEVER,
    /** Fin de la ventana del plan (epoch UTC): "Plan válido hasta…". */
    val validUntil: Long = 0,
)

/** ETag y estado del plan; no son secretos, van en preferencias normales. */
@Singleton
class PlanPrefs
    @Inject
    constructor(
        @param:ApplicationContext context: Context,
    ) {
        private val prefs = context.getSharedPreferences("plan", Context.MODE_PRIVATE)
        private val _info =
            MutableStateFlow(
                PlanInfo(
                    status = PlanStatus.valueOf(prefs.getString(KEY_STATUS, null) ?: PlanStatus.NEVER.name),
                    validUntil = prefs.getLong(KEY_VALID_UNTIL, 0),
                ),
            )
        val info: StateFlow<PlanInfo> = _info.asStateFlow()

        var etag: String?
            get() = prefs.getString(KEY_ETAG, null)
            set(value) = prefs.edit().putString(KEY_ETAG, value).apply()

        fun update(
            status: PlanStatus,
            validUntil: Long = _info.value.validUntil,
        ) {
            prefs
                .edit()
                .putString(KEY_STATUS, status.name)
                .putLong(KEY_VALID_UNTIL, validUntil)
                .apply()
            _info.value = PlanInfo(status, validUntil)
        }

        fun clear() {
            prefs.edit().clear().apply()
            _info.value = PlanInfo()
        }

        private companion object {
            const val KEY_ETAG = "etag"
            const val KEY_STATUS = "status"
            const val KEY_VALID_UNTIL = "validUntil"
        }
    }
