package com.smartwatch.recordatorios.tile

import com.google.common.truth.Truth.assertThat
import com.smartwatch.recordatorios.data.local.DoseEntity
import com.smartwatch.recordatorios.data.local.DoseStatus
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime

class DayTileStateTest {
    private val zone = ZoneId.of("America/Bogota")

    private fun at(
        day: Int,
        hour: Int,
    ) = ZonedDateTime.of(2026, 10, day, hour, 0, 0, 0, zone).toInstant().toEpochMilli()

    private fun dose(
        id: String,
        day: Int,
        hour: Int,
        status: DoseStatus = DoseStatus.SCHEDULED,
    ) = DoseEntity(id, "s$id", "Med $id", "blue", "50 mg", at(day, hour), at(day, hour), status = status)

    @Test
    fun `cuenta las tomadas de hoy y la próxima pendiente`() {
        val doses =
            listOf(
                dose("a", 22, 8, DoseStatus.TAKEN),
                dose("b", 22, 13, DoseStatus.SKIPPED),
                dose("c", 22, 20),
                dose("d", 23, 8),
            )

        val state = dayTileState(doses, at(22, 15), zone)

        assertThat(state.taken).isEqualTo(1)
        assertThat(state.total).isEqualTo(3)
        assertThat(state.next?.id).isEqualTo("c")
        assertThat(state.nextIsLater).isFalse()
        assertThat(state.progress).isWithin(0.001f).of(1f / 3)
    }

    @Test
    fun `sin nada pendiente hoy, la próxima es de mañana`() {
        val doses = listOf(dose("a", 22, 8, DoseStatus.TAKEN), dose("d", 23, 8))

        val state = dayTileState(doses, at(22, 21), zone)

        assertThat(state.next?.id).isEqualTo("d")
        assertThat(state.nextIsLater).isTrue()
        assertThat(state.progress).isEqualTo(1f)
    }

    @Test
    fun `sin dosis hoy el anillo va vacío`() {
        assertThat(dayTileState(emptyList(), at(22, 9), zone).progress).isEqualTo(0f)
    }
}
