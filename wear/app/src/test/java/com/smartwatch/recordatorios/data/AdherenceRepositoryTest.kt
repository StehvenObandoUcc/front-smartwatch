package com.smartwatch.recordatorios.data

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.smartwatch.recordatorios.FakeDosesApi
import com.smartwatch.recordatorios.api.models.DayAdherence
import com.smartwatch.recordatorios.api.models.DeviceAdherence
import com.smartwatch.recordatorios.data.remote.AdherenceDay
import com.smartwatch.recordatorios.data.remote.AdherenceRepository
import com.smartwatch.recordatorios.problem
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Response
import java.math.BigDecimal
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class AdherenceRepositoryTest {
    private val api = FakeDosesApi()
    private val from = LocalDate.of(2026, 10, 6)

    private fun repository() = AdherenceRepository(api, ApplicationProvider.getApplicationContext())

    private fun body(percentage: BigDecimal?) =
        DeviceAdherence(
            from = from,
            to = from.plusDays(1),
            taken = 3,
            skipped = 1,
            missed = 0,
            percentage = percentage,
            days = listOf(DayAdherence(from, 0, 0, 0), DayAdherence(from.plusDays(1), 3, 1, 0)),
        )

    @Test
    fun `guarda la adherencia y la conserva para verla sin conexion`() =
        runTest {
            api.adherence = { Response.success(body(BigDecimal("85.7"))) }

            assertThat(repository().sync()).isTrue()

            // Otra instancia (la app reabierta) lee lo guardado, sin red.
            api.offline = true
            val reopened = repository()
            assertThat(reopened.sync()).isFalse()
            val summary = reopened.summary.value!!
            assertThat(summary.percentage).isEqualTo(86)
            assertThat(summary.from).isEqualTo(from)
            assertThat(summary.days).containsExactly(AdherenceDay(0, 0), AdherenceDay(3, 4)).inOrder()
        }

    @Test
    fun `sin dosis vencidas el porcentaje es nulo`() =
        runTest {
            api.adherence = { Response.success(body(null)) }
            val repository = repository()

            repository.sync()

            assertThat(repository.summary.value!!.percentage).isNull()
        }

    @Test
    fun `sin consentimiento no reintenta y conserva lo anterior`() =
        runTest {
            val repository = repository()
            api.adherence = { Response.success(body(BigDecimal("50"))) }
            repository.sync()
            api.adherence = { problem(403, "consent_required") }

            assertThat(repository.sync()).isTrue()
            assertThat(repository.summary.value!!.percentage).isEqualTo(50)
        }

    @Test
    fun `al vincular otro paciente se borra`() =
        runTest {
            val repository = repository()
            api.adherence = { Response.success(body(BigDecimal("50"))) }
            repository.sync()

            repository.clear()

            assertThat(repository.summary.value).isNull()
            assertThat(repository().summary.value).isNull()
        }
}
