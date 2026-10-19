package com.smartwatch.recordatorios.data.remote

import android.content.Context
import com.smartwatch.recordatorios.api.apis.DosesApi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.IOException
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

/** Dosis vencidas de un día: [taken] de [total] (tomadas + omitidas + no tomadas). */
data class AdherenceDay(
    val taken: Int,
    val total: Int,
)

/** Adherencia de los últimos días; [percentage] es null si aún no venció ninguna dosis. */
data class AdherenceSummary(
    val percentage: Int?,
    val from: LocalDate,
    val days: List<AdherenceDay>,
)

/**
 * Descarga `GET /devices/me/adherence` (14 días por defecto) y guarda el último resultado para
 * mostrarlo sin conexión. No es un secreto: va en preferencias normales.
 */
@Singleton
class AdherenceRepository
    @Inject
    constructor(
        private val api: DosesApi,
        @param:ApplicationContext context: Context,
    ) {
        private val prefs = context.getSharedPreferences("adherence", Context.MODE_PRIVATE)
        private val _summary = MutableStateFlow(load())
        val summary: StateFlow<AdherenceSummary?> = _summary.asStateFlow()

        /** false solo si no hubo red; cualquier otro fallo conserva lo guardado y no reintenta. */
        suspend fun sync(): Boolean {
            val body =
                try {
                    api.getMyDeviceAdherence().body()
                } catch (_: IOException) {
                    return false
                }
            body?.let {
                save(
                    AdherenceSummary(
                        percentage = it.percentage?.toDouble()?.roundToInt(),
                        from = it.from,
                        days = it.days.map { day -> AdherenceDay(day.taken, day.taken + day.skipped + day.missed) },
                    ),
                )
            }
            return true
        }

        fun clear() {
            prefs.edit().clear().apply()
            _summary.value = null
        }

        private fun save(summary: AdherenceSummary) {
            prefs
                .edit()
                .putInt(KEY_PERCENTAGE, summary.percentage ?: NO_PERCENTAGE)
                .putString(KEY_FROM, summary.from.toString())
                .putString(KEY_DAYS, summary.days.joinToString(",") { "${it.taken}/${it.total}" })
                .apply()
            _summary.value = summary
        }

        private fun load(): AdherenceSummary? {
            val from = prefs.getString(KEY_FROM, null) ?: return null
            val days =
                prefs
                    .getString(KEY_DAYS, "")
                    .orEmpty()
                    .split(",")
                    .filter { it.isNotEmpty() }
                    .map { day -> day.split("/").let { AdherenceDay(it[0].toInt(), it[1].toInt()) } }
            val percentage = prefs.getInt(KEY_PERCENTAGE, NO_PERCENTAGE).takeIf { it != NO_PERCENTAGE }
            return AdherenceSummary(percentage, LocalDate.parse(from), days)
        }

        private companion object {
            const val KEY_PERCENTAGE = "percentage"
            const val KEY_FROM = "from"
            const val KEY_DAYS = "days"
            const val NO_PERCENTAGE = -1
        }
    }
