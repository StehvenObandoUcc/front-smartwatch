package com.smartwatch.recordatorios.tile

import com.smartwatch.recordatorios.data.local.DoseEntity
import com.smartwatch.recordatorios.data.local.DoseStatus
import com.smartwatch.recordatorios.ui.screens.home.nextDose
import com.smartwatch.recordatorios.ui.screens.home.todayDoses
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/** Lo que muestra el Tile «Anillo del día»: cuántas van tomadas hoy y la próxima dosis. */
data class DayTileState(
    val taken: Int,
    val total: Int,
    val next: DoseEntity?,
    /** La próxima es de otro día (p. ej. ya no queda nada hoy y la siguiente es mañana). */
    val nextIsLater: Boolean,
) {
    /** Fracción del anillo (0 a 1); sin dosis hoy, el anillo va vacío. */
    val progress: Float get() = if (total == 0) 0f else taken.toFloat() / total
}

/** Textos del Tile, ya resueltos (los pone el servicio desde strings.xml). */
data class DayTileTexts(
    val count: String,
    val takenToday: String,
    /** «20:00 Losartán», o null si no queda ninguna. */
    val next: String?,
    val description: String,
)

fun dayTileState(
    doses: List<DoseEntity>,
    nowMillis: Long,
    zone: ZoneId,
): DayTileState {
    val today = LocalDate.ofInstant(Instant.ofEpochMilli(nowMillis), zone)
    val ofToday = todayDoses(doses, today, zone)
    val next = nextDose(doses, nowMillis)
    val nextDay = next?.let { LocalDate.ofInstant(Instant.ofEpochMilli(it.scheduledAt), zone) }
    return DayTileState(
        taken = ofToday.count { it.status == DoseStatus.TAKEN },
        total = ofToday.size,
        next = next,
        nextIsLater = nextDay != null && nextDay != today,
    )
}
