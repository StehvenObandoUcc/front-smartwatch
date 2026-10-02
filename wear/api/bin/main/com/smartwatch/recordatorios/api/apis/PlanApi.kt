package com.smartwatch.recordatorios.api.apis

import com.smartwatch.recordatorios.api.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import com.smartwatch.recordatorios.api.models.Plan
import com.smartwatch.recordatorios.api.models.Problem

interface PlanApi {
    /**
     * GET devices/me/plan
     * Plan de 7 días del reloj
     * Solo con token de reloj (403 con token de usuario). Devuelve las dosis de los próximos 7 días, desde el inicio del día local del paciente, calculadas con su zona horaria. &#x60;version&#x60; sube con cada cambio de medicamentos u horarios del paciente. El &#x60;ETag&#x60; cambia cuando cambia la versión o el día local; con &#x60;If-None-Match&#x60; igual responde 304 sin cuerpo. Si el paciente no ha dado el consentimiento &#x60;health_data&#x60;, 403 (&#x60;code: consent_required&#x60;). 
     * Responses:
     *  - 200: Plan vigente.
     *  - 304: El plan no cambió.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 403: Autenticado pero sin permiso: rol que no permite la acción sobre un recurso accesible, o tipo de token equivocado (`wrong_token_type`). Nunca se usa para pacientes sin vínculo (eso es 404). 
     *
     * @param ifNoneMatch  (optional)
     * @return [Plan]
     */
    @GET("devices/me/plan")
    suspend fun getMyPlan(@Header("If-None-Match") ifNoneMatch: kotlin.String? = null): Response<Plan>

    /**
     * GET patients/{patientId}/plan
     * Plan de 7 días de un paciente (agenda web)
     * Mismo cuerpo que &#x60;GET /devices/me/plan&#x60;, sin ETag. Para la agenda de hoy y de la semana.
     * Responses:
     *  - 200: Plan vigente.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 403: Autenticado pero sin permiso: rol que no permite la acción sobre un recurso accesible, o tipo de token equivocado (`wrong_token_type`). Nunca se usa para pacientes sin vínculo (eso es 404). 
     *  - 404: No existe o el usuario no tiene acceso: un cuidador que pide un paciente sin vínculo activo (o sus recursos) recibe 404, igual que si no existiera, para no revelar datos ajenos. 
     *
     * @param patientId 
     * @return [Plan]
     */
    @GET("patients/{patientId}/plan")
    suspend fun getPatientPlan(@Path("patientId") patientId: java.util.UUID): Response<Plan>

}
