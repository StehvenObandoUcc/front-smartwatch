package com.smartwatch.recordatorios.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.smartwatch.recordatorios.TestGraph
import com.smartwatch.recordatorios.TestGraph.Companion.MINUTE
import com.smartwatch.recordatorios.TestGraph.Companion.T0
import com.smartwatch.recordatorios.data.local.DoseAction
import com.smartwatch.recordatorios.data.local.DoseStatus
import com.smartwatch.recordatorios.data.local.SyncState
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class DoseRepositoryTest {
    private lateinit var graph: TestGraph

    @Before
    fun setUp() {
        graph = TestGraph()
    }

    @After
    fun tearDown() = graph.close()

    @Test
    fun `la prueba crea 2 dosis falsas en 1 y 2 minutos`() =
        runTest {
            val doses = graph.repository.seedDemoDoses()

            assertThat(doses).hasSize(2)
            assertThat(doses.map { it.nextAlarmAt }).containsExactly(T0 + MINUTE, T0 + 2 * MINUTE)
            assertThat(graph.repository.scheduledDoses()).hasSize(2)
        }

    @Test
    fun `seedDemoIfEmpty no pisa un plan existente`() =
        runTest {
            graph.repository.seedDemoDoses()
            graph.repository.record("demo-1", DoseAction.TAKEN)

            assertThat(graph.repository.seedDemoIfEmpty()).isNull()
            assertThat(graph.repository.get("demo-1")?.status).isEqualTo(DoseStatus.TAKEN)
        }

    @Test
    fun `tomada guarda un evento PENDING con eventId UUID`() =
        runTest {
            graph.repository.seedDemoDoses()

            val updated = graph.repository.record("demo-1", DoseAction.TAKEN)

            assertThat(updated?.status).isEqualTo(DoseStatus.TAKEN)
            val event = graph.repository.eventsFor("demo-1").single()
            assertThat(event.action).isEqualTo(DoseAction.TAKEN)
            assertThat(event.syncState).isEqualTo(SyncState.PENDING)
            assertThat(event.occurredAt).isEqualTo(T0)
            assertThat(UUID.fromString(event.eventId).toString()).isEqualTo(event.eventId)
        }

    @Test
    fun `una segunda accion sobre una dosis resuelta se ignora`() =
        runTest {
            graph.repository.seedDemoDoses()
            graph.repository.record("demo-1", DoseAction.TAKEN)

            assertThat(graph.repository.record("demo-1", DoseAction.TAKEN)).isNull()
            assertThat(graph.repository.record("demo-1", DoseAction.SKIPPED)).isNull()
            assertThat(graph.repository.eventsFor("demo-1")).hasSize(1)
        }

    @Test
    fun `posponer mueve la alarma 10 minutos y se limita a 3 veces`() =
        runTest {
            graph.repository.seedDemoDoses()

            repeat(3) { i ->
                graph.now = T0 + i * MINUTE
                val snoozed = graph.repository.record("demo-1", DoseAction.SNOOZED)
                assertThat(snoozed?.snoozeCount).isEqualTo(i + 1)
                assertThat(snoozed?.nextAlarmAt).isEqualTo(graph.now + 10 * MINUTE)
                assertThat(snoozed?.status).isEqualTo(DoseStatus.SCHEDULED)
            }

            assertThat(graph.repository.record("demo-1", DoseAction.SNOOZED)).isNull()
            assertThat(graph.repository.eventsFor("demo-1")).hasSize(3)
        }

    @Test
    fun `omitir marca la dosis como omitida`() =
        runTest {
            graph.repository.seedDemoDoses()

            val skipped = graph.repository.record("demo-2", DoseAction.SKIPPED)

            assertThat(skipped?.status).isEqualTo(DoseStatus.SKIPPED)
            assertThat(graph.repository.scheduledDoses().map { it.id }).containsExactly("demo-1")
        }

    @Test
    fun `accion sobre una dosis inexistente devuelve null`() =
        runTest {
            assertThat(graph.repository.record("nope", DoseAction.TAKEN)).isNull()
        }
}
