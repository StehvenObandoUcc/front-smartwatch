package com.smartwatch.recordatorios.ui.components

import com.smartwatch.recordatorios.data.local.DoseEntity
import com.smartwatch.recordatorios.data.local.DoseStatus

/** Estado de una casilla del pastillero. Cada uno se distingue también sin color (visto, borde, raya). */
enum class PillState { DONE, NEXT, LATER, SKIPPED }

/**
 * Las dosis del día en orden: tomadas, omitidas, la siguiente y el resto, más tarde. La siguiente es
 * [nextId], la misma que Inicio muestra en grande (una pendiente que ya pasó no lo es); sin ella, la
 * primera pendiente.
 */
fun pillStates(
    doses: List<DoseEntity>,
    nextId: String? = null,
): List<PillState> {
    val sorted = doses.sortedBy { it.scheduledAt }
    val next = nextId ?: sorted.firstOrNull { it.status == DoseStatus.SCHEDULED }?.id
    return sorted.map {
        when {
            it.status == DoseStatus.TAKEN -> PillState.DONE
            it.status == DoseStatus.SKIPPED -> PillState.SKIPPED
            it.id == next -> PillState.NEXT
            else -> PillState.LATER
        }
    }
}
