package com.smartwatch.recordatorios.api.apis

import com.smartwatch.recordatorios.api.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import com.smartwatch.recordatorios.api.models.Adherence
import com.smartwatch.recordatorios.api.models.DoseEventBatch
import com.smartwatch.recordatorios.api.models.DoseEventBatchResult
import com.smartwatch.recordatorios.api.models.DoseHistoryPage
import com.smartwatch.recordatorios.api.models.Problem
import com.smartwatch.recordatorios.api.models.ValidationProblem

interface DosesApi {
    /**
     * POST devices/me/dose-events
     * Subir eventos de toma (lote, idempotente)
     * Solo con token de reloj. Hasta 100 eventos por lote. Idempotente por &#x60;eventId&#x60;: reenviar un evento ya guardado devuelve &#x60;outcome: duplicate&#x60; sin duplicar nada. Cada evento se resuelve por separado y el lote responde 200; un evento inválido no hace fallar a los demás. Un evento solo se acepta si &#x60;scheduleId&#x60; pertenece al paciente del reloj y &#x60;scheduledAt&#x60; es una hora que ese horario generaba; si no, &#x60;outcome: rejected&#x60; con &#x60;code&#x60; (&#x60;schedule_not_found&#x60;, &#x60;invalid_scheduled_at&#x60;). Una dosis tiene como mucho un evento: si llega otro &#x60;eventId&#x60; para la misma dosis (&#x60;scheduleId&#x60; + &#x60;scheduledAt&#x60;), gana el primero y el segundo se rechaza con &#x60;code: dose_already_recorded&#x60;. 
     * Responses:
     *  - 200: Resultado por evento, en el mismo orden.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 403: Autenticado pero sin permiso: rol que no permite la acción sobre un recurso accesible, o tipo de token equivocado (`wrong_token_type`). Nunca se usa para pacientes sin vínculo (eso es 404). 
     *  - 422: La petición no cumple el esquema.
     *
     * @param doseEventBatch 
     * @return [DoseEventBatchResult]
     */
    @POST("devices/me/dose-events")
    suspend fun createMyDoseEvents(@Body doseEventBatch: DoseEventBatch): Response<DoseEventBatchResult>

    /**
     * GET patients/{patientId}/adherence
     * Porcentaje de adherencia de un paciente
     * &#x60;percentage &#x3D; taken / (taken + skipped + missed) * 100&#x60; sobre las dosis vencidas del rango (mismas reglas y rango por defecto que el historial). &#x60;null&#x60; si no hubo dosis vencidas. 
     * Responses:
     *  - 200: Adherencia del rango.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 403: Autenticado pero sin permiso: rol que no permite la acción sobre un recurso accesible, o tipo de token equivocado (`wrong_token_type`). Nunca se usa para pacientes sin vínculo (eso es 404). 
     *  - 404: No existe o el usuario no tiene acceso: un cuidador que pide un paciente sin vínculo activo (o sus recursos) recibe 404, igual que si no existiera, para no revelar datos ajenos. 
     *  - 422: La petición no cumple el esquema.
     *
     * @param patientId 
     * @param from Inicio del rango (inclusive), fecha local del paciente. (optional)
     * @param to Fin del rango (inclusive), fecha local del paciente. (optional)
     * @return [Adherence]
     */
    @GET("patients/{patientId}/adherence")
    suspend fun getAdherence(@Path("patientId") patientId: java.util.UUID, @Query("from") from: java.time.LocalDate? = null, @Query("to") to: java.time.LocalDate? = null): Response<Adherence>

    /**
     * GET patients/{patientId}/dose-history
     * Historial de dosis de un paciente
     * Dosis ya vencidas, de la más reciente a la más antigua, con su estado. Una dosis sin evento cuya hora programada pasó hace más de 60 minutos se informa como &#x60;MISSED&#x60; (se calcula al consultar). Las dosis sin evento futuras o dentro de la ventana de 60 min no aparecen; una dosis con evento aparece siempre. Solo cuentan las dosis que el horario ya generaba cuando se creó o editó, y mientras el medicamento no estaba archivado. Rango por defecto: últimos 7 días; máximo 90 días, y &#x60;from&#x60; no puede ser posterior a &#x60;to&#x60; (422). 
     * Responses:
     *  - 200: Página del historial.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 403: Autenticado pero sin permiso: rol que no permite la acción sobre un recurso accesible, o tipo de token equivocado (`wrong_token_type`). Nunca se usa para pacientes sin vínculo (eso es 404). 
     *  - 404: No existe o el usuario no tiene acceso: un cuidador que pide un paciente sin vínculo activo (o sus recursos) recibe 404, igual que si no existiera, para no revelar datos ajenos. 
     *  - 422: La petición no cumple el esquema.
     *
     * @param patientId 
     * @param cursor Cursor opaco devuelto como &#x60;nextCursor&#x60; en la página anterior. (optional)
     * @param limit  (optional, default to 20)
     * @param from Inicio del rango (inclusive), fecha local del paciente. (optional)
     * @param to Fin del rango (inclusive), fecha local del paciente. (optional)
     * @return [DoseHistoryPage]
     */
    @GET("patients/{patientId}/dose-history")
    suspend fun listDoseHistory(@Path("patientId") patientId: java.util.UUID, @Query("cursor") cursor: kotlin.String? = null, @Query("limit") limit: kotlin.Int? = 20, @Query("from") from: java.time.LocalDate? = null, @Query("to") to: java.time.LocalDate? = null): Response<DoseHistoryPage>

}
