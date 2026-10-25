package com.smartwatch.recordatorios.ui

import com.google.common.truth.Truth.assertThat
import com.smartwatch.recordatorios.data.local.DoseEntity
import com.smartwatch.recordatorios.data.local.DoseStatus
import com.smartwatch.recordatorios.data.remote.AdherenceDay
import com.smartwatch.recordatorios.ui.screens.adherence.DayResult
import com.smartwatch.recordatorios.ui.screens.adherence.dayResult
import com.smartwatch.recordatorios.ui.screens.history.HistoryState
import com.smartwatch.recordatorios.ui.screens.history.historyEntries
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime

class HistoryTest {
    private val zone = ZoneId.of("America/Bogota")

    private fun at(
        day: Int,
        hour: Int,
        minute: Int = 0,
    ) = ZonedDateTime.of(2026, 10, day, hour, minute, 0, 0, zone).toInstant().toEpochMilli()

    private fun dose(
        id: String,
        day: Int,
        hour: Int,
        status: DoseStatus = DoseStatus.SCHEDULED,
    ) = DoseEntity(id, "s$id", "Med", "blue", "50 mg", at(day, hour), at(day, hour), status = status)

    @Test
    fun `el historial solo tiene lo que ya llego a su hora, de lo mas reciente a lo mas antiguo`() {
        val doses =
            listOf(
                dose("a", 24, 8, DoseStatus.TAKEN),
                dose("b", 24, 13),
                dose("c", 24, 20),
                dose("d", 23, 20, DoseStatus.SKIPPED),
                dose("old", 10, 8, DoseStatus.TAKEN),
            )

        val entries = historyEntries(doses, now = at(24, 13, 30), zone = zone)

        assertThat(entries.map { it.dose.id }).containsExactly("b", "a", "d").inOrder()
        assertThat(entries.map { it.state })
            .containsExactly(HistoryState.DUE, HistoryState.TAKEN, HistoryState.SKIPPED)
            .inOrder()
    }

    @Test
    fun `sin registrar pasa a no tomada a los 60 minutos`() {
        val entries = historyEntries(listOf(dose("b", 24, 13)), now = at(24, 14, 1), zone = zone)

        assertThat(entries.single().state).isEqualTo(HistoryState.MISSED)
    }

    @Test
    fun `cada dia del calendario dice como termino`() {
        assertThat(dayResult(AdherenceDay(taken = 2, total = 2))).isEqualTo(DayResult.ALL_TAKEN)
        assertThat(dayResult(AdherenceDay(taken = 1, total = 2))).isEqualTo(DayResult.SOME_MISSED)
        assertThat(dayResult(AdherenceDay(taken = 0, total = 0))).isEqualTo(DayResult.NO_DOSES)
    }
}
