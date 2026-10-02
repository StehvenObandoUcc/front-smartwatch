package com.smartwatch.recordatorios.api.apis

import com.smartwatch.recordatorios.api.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import com.smartwatch.recordatorios.api.models.Health
import com.smartwatch.recordatorios.api.models.Problem
import com.smartwatch.recordatorios.api.models.Readiness

interface HealthApi {
    /**
     * GET health
     * Liveness
     * Indica que el proceso está vivo. No consulta dependencias.
     * Responses:
     *  - 200: El servicio está vivo.
     *
     * @return [Health]
     */
    @GET("health")
    suspend fun getHealth(): Response<Health>

    /**
     * GET health/ready
     * Readiness
     * Comprueba las dependencias (Postgres y Redis).
     * Responses:
     *  - 200: Todas las dependencias responden.
     *  - 503: Alguna dependencia no responde.
     *
     * @return [Readiness]
     */
    @GET("health/ready")
    suspend fun getReadiness(): Response<Readiness>

}
