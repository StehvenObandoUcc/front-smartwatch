package com.smartwatch.recordatorios

import com.smartwatch.recordatorios.api.apis.DosesApi
import com.smartwatch.recordatorios.api.apis.PlanApi
import com.smartwatch.recordatorios.api.models.Adherence
import com.smartwatch.recordatorios.api.models.DoseEventBatch
import com.smartwatch.recordatorios.api.models.DoseEventBatchResult
import com.smartwatch.recordatorios.api.models.DoseEventResult
import com.smartwatch.recordatorios.api.models.DoseHistoryPage
import com.smartwatch.recordatorios.api.models.Plan
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.io.IOException
import java.time.LocalDate
import java.util.UUID

/** `getMyPlan` devuelve lo que diga `next` y recuerda el If-None-Match recibido. */
class FakePlanApi : PlanApi {
    var next: () -> Response<Plan> = { error("sin respuesta configurada") }
    var lastIfNoneMatch: String? = null

    override suspend fun getMyPlan(ifNoneMatch: String?): Response<Plan> {
        lastIfNoneMatch = ifNoneMatch
        return next()
    }

    override suspend fun getPatientPlan(patientId: UUID): Response<Plan> = error("no se usa en el reloj")
}

class FakeDosesApi : DosesApi {
    val batches = mutableListOf<DoseEventBatch>()
    var offline = false

    /** Resultado por evento; por omisión todos `created`. */
    var outcome: (UUID) -> DoseEventResult.Outcome = { DoseEventResult.Outcome.created }

    override suspend fun createMyDoseEvents(doseEventBatch: DoseEventBatch): Response<DoseEventBatchResult> {
        if (offline) throw IOException("sin red")
        batches += doseEventBatch
        val results = doseEventBatch.events.map { DoseEventResult(it.eventId, outcome(it.eventId)) }
        return Response.success(DoseEventBatchResult(results))
    }

    override suspend fun getAdherence(
        patientId: UUID,
        from: LocalDate?,
        to: LocalDate?,
    ): Response<Adherence> = error("no se usa en el reloj")

    override suspend fun listDoseHistory(
        patientId: UUID,
        cursor: String?,
        limit: Int?,
        from: LocalDate?,
        to: LocalDate?,
    ): Response<DoseHistoryPage> = error("no se usa en el reloj")
}

fun notModified(): Response<Plan> {
    val raw =
        okhttp3.Response
            .Builder()
            .code(304)
            .message("Not Modified")
            .protocol(Protocol.HTTP_1_1)
            .request(Request.Builder().url("http://localhost/").build())
            .build()
    return Response.error("".toResponseBody(), raw)
}

fun problem(
    status: Int,
    code: String,
): Response<Plan> =
    Response.error(status, """{"code":"$code"}""".toResponseBody("application/problem+json".toMediaType()))
