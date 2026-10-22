package com.smartwatch.recordatorios.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.smartwatch.recordatorios.TestGraph
import com.smartwatch.recordatorios.TestGraph.Companion.T0
import com.smartwatch.recordatorios.api.models.Plan
import com.smartwatch.recordatorios.api.models.PlannedDose
import com.smartwatch.recordatorios.data.local.DoseAction
import com.smartwatch.recordatorios.data.local.DoseStatus
import com.smartwatch.recordatorios.data.repository.isLocal
import kotlinx.coroutines.test.runTest
import okhttp3.Headers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.shadows.ShadowAlarmManager
import retrofit2.Response
import java.time.Instant
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.TimeZone
import java.util.UUID

/** T0 = 2026-01-15 12:00 UTC; el reloj de la prueba está en UTC. */
@RunWith(AndroidJUnit4::class)
class LocalPlanRepositoryTest {
    private lateinit var graph: TestGraph
    private val originalZone = TimeZone.getDefault()

    @Before
    fun setUp() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
        graph = TestGraph()
        ShadowAlarmManager.setCanScheduleExactAlarms(true)
    }

    @After
    fun tearDown() {
        graph.close()
        TimeZone.setDefault(originalZone)
    }

    private suspend fun addLosartan() =
        graph.localPlan.add("Losartán", "50 mg", listOf(LocalTime.of(20, 0), LocalTime.of(8, 0)))

    @Test
    fun `crear un medicamento genera las dosis futuras de 7 dias con sus alarmas`() =
        runTest {
            addLosartan()

            val doses = graph.repository.scheduledDoses()
            // Hoy solo la de las 20:00 (las 08:00 ya pasaron); luego 2 por día durante 6 días.
            assertThat(doses).hasSize(13)
            assertThat(doses.all { isLocal(it.scheduleId) && it.medicationName == "Losartán" }).isTrue()
            assertThat(doses.first().scheduledAt).isEqualTo(T0 + 8 * HOUR)
            assertThat(graph.shadowAlarms.scheduledAlarms).hasSize(13)
        }

    @Test
    fun `rellenar otra vez no duplica ni pisa lo ya tomado`() =
        runTest {
            addLosartan()
            val first = graph.repository.scheduledDoses().first()
            graph.now = first.scheduledAt
            graph.repository.record(first.id, DoseAction.TAKEN)

            graph.localPlan.refill()

            assertThat(graph.db.doseDao().all()).hasSize(13)
            assertThat(graph.repository.get(first.id)?.status).isEqualTo(DoseStatus.TAKEN)
        }

    @Test
    fun `sincronizar el plan del backend conserva las dosis locales`() =
        runTest {
            addLosartan()
            val at = OffsetDateTime.ofInstant(Instant.ofEpochMilli(T0 + HOUR), ZoneOffset.UTC)
            val remote = PlannedDose(UUID.randomUUID(), UUID.randomUUID(), "Metformina", "850 mg", "#d4661a", at)
            val plan = Plan(1, "UTC", at, at, at.plusDays(7), listOf(remote))
            graph.planApi.next = { Response.success(plan, Headers.headersOf("ETag", "\"1\"")) }

            graph.planRepository.sync()

            val names = graph.repository.scheduledDoses().map { it.medicationName }
            assertThat(names.count { it == "Losartán" }).isEqualTo(13)
            assertThat(names).contains("Metformina")
        }

    @Test
    fun `los eventos de dosis locales no se suben al backend`() =
        runTest {
            addLosartan()
            val dose = graph.repository.scheduledDoses().first()
            graph.now = dose.scheduledAt
            graph.repository.record(dose.id, DoseAction.TAKEN)

            assertThat(graph.uploader.upload()).isTrue()
            assertThat(graph.dosesApi.batches).isEmpty()
        }

    @Test
    fun `quitar un medicamento borra sus dosis pendientes y cancela las alarmas`() =
        runTest {
            val medication = addLosartan()

            graph.localPlan.remove(medication.id)

            assertThat(graph.db.doseDao().all()).isEmpty()
            assertThat(graph.shadowAlarms.scheduledAlarms).isEmpty()
        }

    private companion object {
        const val HOUR = 3_600_000L
    }
}
