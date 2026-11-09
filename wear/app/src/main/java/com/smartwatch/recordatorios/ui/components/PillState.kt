package com.smartwatch.recordatorios.ui.components

import com.smartwatch.recordatorios.data.local.DoseEntity
import com.smartwatch.recordatorios.data.local.DoseStatus

/** Estado de una casilla del pastillero. Cada uno se distingue también sin color (visto, borde, raya). */
enum class PillState { DONE, NEXT, LATER, SKIPPED }

/** Las dosis del día en orden: tomadas, omitidas, la primera pendiente es la siguiente y el resto, más tarde. */
fun pillStates(doses: List<DoseEntity>): List<PillState> {
    val sorted = doses.sortedBy { it.scheduledAt }
    val nextId = sorted.firstOrNull { it.status == DoseStatus.SCHEDULED }?.id
    return sorted.map {
        when {
            it.status == DoseStatus.TAKEN -> PillState.DONE
            it.status == DoseStatus.SKIPPED -> PillState.SKIPPED
            it.id == nextId -> PillState.NEXT
            else -> PillState.LATER
        }
    }
}
