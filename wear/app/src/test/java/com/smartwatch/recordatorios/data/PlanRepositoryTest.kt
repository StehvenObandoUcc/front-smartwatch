package com.smartwatch.recordatorios.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.smartwatch.recordatorios.TestGraph
import com.smartwatch.recordatorios.TestGraph.Companion.MINUTE
import com.smartwatch.recordatorios.TestGraph.Companion.T0
import com.smartwatch.recordatorios.api.models.Plan
import com.smartwatch.recordatorios.api.models.PlannedDose
import com.smartwatch.recordatorios.data.local.DoseAction
import com.smartwatch.recordatorios.data.local.DoseStatus
import com.smartwatch.recordatorios.data.remote.PlanStatus
import com.smartwatch.recordatorios.data.remote.SyncResult
import com.smartwatch.recordatorios.notModified
import com.smartwatch.recordatorios.problem
import kotlinx.coroutines.test.runTest
import okhttp3.Headers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.shadows.ShadowAlarmManager
import retrofit2.Response
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class PlanRepositoryTest {
    private lateinit var graph: TestGraph
    private val scheduleA = UUID.randomUUID()
    private val scheduleB = UUID.randomUUID()

    @Before
    fun setUp() {
        graph = TestGraph()
        ShadowAlarmManager.setCanScheduleExactAlarms(true)
    }

    @After
    fun tearDown() = graph.close()

    private fun at(offsetMinutes: Int): OffsetDateTime =
        OffsetDateTime.ofInstant(Instant.ofEpochMilli(T0 + offsetMinutes * MINUTE), ZoneOffset.UTC)

    private fun dose(
        schedule: UUID,
        offsetMinutes: Int,
        name: String = "Losartán",
        color: String = "#1565C0",
    ) = PlannedDose(schedule, UUID.randomUUID(), name, "1 tableta", color, at(offsetMinutes))

    private fun plan(vararg doses: PlannedDose) = Plan(1, "America/Bogota", at(0), at(-720), at(10_080), doses.toList())

    private fun ok(
        plan: Plan,
        etag: String = "\"1-2026-01-15\"",
    ): Response<Plan> = Response.success(plan, Headers.headersOf("ETag", etag))

    @Test
    fun `una descarga guarda las dosis, programa las alarmas y recuerda el ETag`() =
        runTest {
            graph.planApi.next = { ok(plan(dose(scheduleA, 60), dose(scheduleA, 120, "Metformina", "#d4661a"))) }

            assertThat(graph.planRepository.sync()).isEqualTo(SyncResult.UPDATED)

            val doses = graph.repository.scheduledDoses()
            assertThat(doses.map { it.medicationName }).containsExactly("Losartán", "Metformina").inOrder()
            assertThat(doses.map { it.colorKey }).containsExactly("blue", "orange").inOrder()
            assertThat(graph.shadowAlarms.scheduledAlarms.map { it.triggerAtMs })
                .containsExactly(T0 + 60 * MINUTE, T0 + 120 * MINUTE)
            assertThat(graph.planPrefs.etag).isEqualTo("\"1-2026-01-15\"")
            assertThat(graph.planPrefs.info.value.status).isEqualTo(PlanStatus.OK)
        }

    @Test
    fun `la siguiente consulta envia If-None-Match y un 304 no toca nada`() =
        runTest {
            graph.planApi.next = { ok(plan(dose(scheduleA, 60))) }
            graph.planRepository.sync()
            graph.planApi.next = { notModified() }

            assertThat(graph.planRepository.sync()).isEqualTo(SyncResult.UNCHANGED)

            assertThat(graph.planApi.lastIfNoneMatch).isEqualTo("\"1-2026-01-15\"")
            assertThat(graph.repository.scheduledDoses()).hasSize(1)
        }

    @Test
    fun `un plan nuevo conserva lo ya tomado y retira lo que ya no esta`() =
        runTest {
            val keep = dose(scheduleA, 60)
            val taken = dose(scheduleA, 120)
            val removed = dose(scheduleB, 180)
            graph.planApi.next = { ok(plan(keep, taken, removed)) }
            graph.planRepository.sync()
            val takenId =
                graph.repository
                    .scheduledDoses()
                    .first { it.nextAlarmAt == T0 + 120 * MINUTE }
                    .id
            graph.repository.record(takenId, DoseAction.TAKEN)
            graph.scheduler.cancel(takenId)

            val newDose = dose(scheduleB, 240)
            graph.planApi.next = { ok(plan(keep, taken, newDose), "\"2-2026-01-15\"") }
            graph.planRepository.sync()

            val all = graph.db.doseDao().all()
            assertThat(all.map { it.scheduleId to it.nextAlarmAt })
                .containsExactly(
                    scheduleA.toString() to T0 + 60 * MINUTE,
                    scheduleA.toString() to T0 + 120 * MINUTE,
                    scheduleB.toString() to T0 + 240 * MINUTE,
                )
            assertThat(all.first { it.id == takenId }.status).isEqualTo(DoseStatus.TAKEN)
            // Solo suenan las pendientes: la tomada no se reprograma y la retirada se canceló.
            assertThat(graph.shadowAlarms.scheduledAlarms.map { it.triggerAtMs })
                .containsExactly(T0 + 60 * MINUTE, T0 + 240 * MINUTE)
        }

    @Test
    fun `una dosis pospuesta mantiene su nueva hora tras sincronizar`() =
        runTest {
            graph.planApi.next = { ok(plan(dose(scheduleA, 60))) }
            graph.planRepository.sync()
            val id =
                graph.repository
                    .scheduledDoses()
                    .single()
                    .id
            graph.now = T0 + 60 * MINUTE
            graph.repository.record(id, DoseAction.SNOOZED)

            graph.planApi.next = { ok(plan(dose(scheduleA, 60)), "\"2-2026-01-15\"") }
            graph.planRepository.sync()

            assertThat(graph.repository.get(id)?.nextAlarmAt).isEqualTo(T0 + 70 * MINUTE)
        }

    @Test
    fun `sin consentimiento el backend responde 403 y se avisa en Inicio`() =
        runTest {
            graph.planApi.next = { problem(403, "consent_required") }

            assertThat(graph.planRepository.sync()).isEqualTo(SyncResult.CONSENT_REQUIRED)
            assertThat(graph.planPrefs.info.value.status).isEqualTo(PlanStatus.CONSENT_REQUIRED)
        }

    @Test
    fun `un error del servidor se reintenta mas tarde`() =
        runTest {
            graph.planApi.next = { problem(500, "internal_error") }

            assertThat(graph.planRepository.sync()).isEqualTo(SyncResult.FAILED)
        }
}
