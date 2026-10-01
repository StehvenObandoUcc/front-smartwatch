package com.smartwatch.recordatorios.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.smartwatch.recordatorios.TestGraph
import com.smartwatch.recordatorios.TestGraph.Companion.MINUTE
import com.smartwatch.recordatorios.TestGraph.Companion.T0
import com.smartwatch.recordatorios.api.models.DoseEventResult
import com.smartwatch.recordatorios.api.models.DoseEventStatus
import com.smartwatch.recordatorios.data.local.DoseAction
import com.smartwatch.recordatorios.data.local.SyncState
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class EventUploaderTest {
    private lateinit var graph: TestGraph

    @Before
    fun setUp() {
        graph = TestGraph()
    }

    @After
    fun tearDown() = graph.close()

    private suspend fun states() =
        graph.db.doseEventDao().let { dao ->
            listOf("demo-1", "demo-2").flatMap {
                dao.forDose(it)
            }
        }

    @Test
    fun `sube Tomada y Omitida en un lote y las marca CONFIRMED`() =
        runTest {
            graph.seed()
            graph.now = T0 + MINUTE
            graph.repository.record("demo-1", DoseAction.TAKEN)
            graph.repository.record("demo-2", DoseAction.SKIPPED)

            assertThat(graph.uploader.upload()).isTrue()

            val sent =
                graph.dosesApi.batches
                    .single()
                    .events
            assertThat(sent.map { it.status }).containsExactly(DoseEventStatus.TAKEN, DoseEventStatus.SKIPPED)
            assertThat(sent.first().scheduleId.toString()).isEqualTo("00000000-0000-0000-0000-000000000001")
            assertThat(states().map { it.syncState }).containsExactly(SyncState.CONFIRMED, SyncState.CONFIRMED)
        }

    @Test
    fun `sin red los eventos siguen PENDING y se reintentan`() =
        runTest {
            graph.seed()
            graph.repository.record("demo-1", DoseAction.TAKEN)
            graph.dosesApi.offline = true

            assertThat(graph.uploader.upload()).isFalse()
            assertThat(states().single().syncState).isEqualTo(SyncState.PENDING)

            graph.dosesApi.offline = false
            assertThat(graph.uploader.upload()).isTrue()
            assertThat(states().single().syncState).isEqualTo(SyncState.CONFIRMED)
        }

    @Test
    fun `duplicate y rejected son definitivos y no se reenvian`() =
        runTest {
            graph.seed()
            graph.repository.record("demo-1", DoseAction.TAKEN)
            graph.repository.record("demo-2", DoseAction.TAKEN)
            graph.dosesApi.outcome =
                { id: UUID ->
                    if (id.hashCode() % 2 ==
                        0
                    ) {
                        DoseEventResult.Outcome.duplicate
                    } else {
                        DoseEventResult.Outcome.rejected
                    }
                }

            assertThat(graph.uploader.upload()).isTrue()
            assertThat(graph.uploader.upload()).isTrue()

            assertThat(graph.dosesApi.batches).hasSize(1)
        }

    @Test
    fun `posponer es local y nunca se sube`() =
        runTest {
            graph.seed()
            graph.now = T0 + MINUTE
            graph.repository.record("demo-1", DoseAction.SNOOZED)

            assertThat(graph.uploader.upload()).isTrue()

            assertThat(graph.dosesApi.batches).isEmpty()
        }
}
