package com.smartwatch.recordatorios.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.smartwatch.recordatorios.FakeMedicationsApi
import com.smartwatch.recordatorios.TestGraph
import com.smartwatch.recordatorios.api.models.MedicationWithSchedule
import com.smartwatch.recordatorios.data.remote.LocalMedicationUploader
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.shadows.ShadowAlarmManager
import retrofit2.Response
import java.io.IOException
import java.time.LocalTime
import java.util.TimeZone

@RunWith(AndroidJUnit4::class)
class LocalMedicationUploaderTest {
    private lateinit var graph: TestGraph
    private val originalZone = TimeZone.getDefault()
    private val api = FakeMedicationsApi().apply { create = { Response.success<MedicationWithSchedule>(201, null) } }

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

    private fun uploader() = LocalMedicationUploader(api, graph.db, graph.localPlan, graph.clock)

    @Test
    fun `sube cada medicamento local con su id como clientId y lo quita del reloj`() =
        runTest {
            val local = graph.localPlan.add("Losartán", "50 mg", listOf(LocalTime.of(8, 0), LocalTime.of(20, 0)))

            assertThat(uploader().upload()).isTrue()

            val request = api.sent.single()
            assertThat(request.clientId.toString()).isEqualTo(local.id)
            assertThat(request.name).isEqualTo("Losartán")
            assertThat(request.schedule.times).containsExactly("08:00", "20:00")
            assertThat(request.schedule.daysOfWeek).containsExactly(1, 2, 3, 4, 5, 6, 7)
            assertThat(request.color).isEqualTo("#1565c0")
            assertThat(graph.db.localMedicationDao().all()).isEmpty()
            assertThat(graph.db.doseDao().all()).isEmpty()
        }

    @Test
    fun `sin red se queda en el reloj y pide reintentar`() =
        runTest {
            graph.localPlan.add("Losartán", "50 mg", listOf(LocalTime.of(20, 0)))
            api.create = { throw IOException("sin red") }

            assertThat(uploader().upload()).isFalse()
            assertThat(graph.db.localMedicationDao().all()).hasSize(1)
        }

    @Test
    fun `si el backend lo rechaza sigue sonando en el reloj sin reintentar en bucle`() =
        runTest {
            graph.localPlan.add("Losartán", "50 mg", listOf(LocalTime.of(20, 0)))
            api.create = {
                Response.error(
                    403,
                    """{"code":"consent_required"}""".toResponseBody("application/problem+json".toMediaType()),
                )
            }

            assertThat(uploader().upload()).isTrue()
            assertThat(graph.db.localMedicationDao().all()).hasSize(1)
            assertThat(graph.repository.scheduledDoses()).isNotEmpty()
        }
}
