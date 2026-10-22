package com.smartwatch.recordatorios.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.smartwatch.recordatorios.TestGraph
import com.smartwatch.recordatorios.TestGraph.Companion.MINUTE
import com.smartwatch.recordatorios.TestGraph.Companion.T0
import com.smartwatch.recordatorios.api.apis.DevicesApi
import com.smartwatch.recordatorios.data.local.DoseAction
import com.smartwatch.recordatorios.data.remote.AdherenceRepository
import com.smartwatch.recordatorios.data.remote.PairingRepository
import com.smartwatch.recordatorios.data.remote.TokenStore
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.shadows.ShadowAlarmManager
import java.lang.reflect.Proxy

@RunWith(AndroidJUnit4::class)
class UnpairTest {
    private lateinit var graph: TestGraph

    @Before
    fun setUp() {
        graph = TestGraph()
        ShadowAlarmManager.setCanScheduleExactAlarms(true)
    }

    @After
    fun tearDown() = graph.close()

    // Desvincular no llama a la API de vinculación: basta un proxy que falle si se usa.
    private val unusedDevicesApi =
        Proxy.newProxyInstance(DevicesApi::class.java.classLoader, arrayOf(DevicesApi::class.java)) { _, _, _ ->
            error("no se usa al desvincular")
        } as DevicesApi

    private fun pairing() =
        PairingRepository(
            unusedDevicesApi,
            TokenStore(graph.context),
            graph.planRepository,
            AdherenceRepository(graph.dosesApi, graph.context),
            graph.uploader,
            graph.db,
        )

    @Test
    fun `desvincular sube lo pendiente y borra dosis, alarmas y eventos`() =
        runTest {
            graph.seed().forEach { graph.scheduler.schedule(it) }
            graph.now = T0 + MINUTE
            graph.repository.record("demo-1", DoseAction.TAKEN)

            pairing().unpair()

            assertThat(graph.dosesApi.batches.flatMap { it.events }).hasSize(1)
            assertThat(graph.db.doseDao().all()).isEmpty()
            assertThat(graph.db.doseEventDao().forDose("demo-1")).isEmpty()
            assertThat(graph.shadowAlarms.scheduledAlarms).isEmpty()
        }

    @Test
    fun `sin red desvincula igual (no deja al reloj atado a un paciente)`() =
        runTest {
            graph.seed()
            graph.dosesApi.offline = true
            graph.repository.record("demo-1", DoseAction.TAKEN)

            pairing().unpair()

            assertThat(graph.db.doseDao().all()).isEmpty()
            assertThat(TokenStore(graph.context).paired.value).isFalse()
        }
}
