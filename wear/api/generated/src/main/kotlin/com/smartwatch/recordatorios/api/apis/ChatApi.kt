package com.smartwatch.recordatorios.api.apis

import com.smartwatch.recordatorios.api.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import com.smartwatch.recordatorios.api.models.ChatRequest
import com.smartwatch.recordatorios.api.models.Problem
import com.smartwatch.recordatorios.api.models.ValidationProblem
import com.smartwatch.recordatorios.api.models.WatchChatReply

interface ChatApi {
    /**
     * POST patients/{patientId}/chat/messages
     * Preguntar al asistente sobre el plan de un paciente (web, SSE)
     * La respuesta llega como flujo &#x60;text/event-stream&#x60; (SSE) con tres tipos de evento: - &#x60;event: delta&#x60; con &#x60;data: {\&quot;text\&quot;: \&quot;...\&quot;}&#x60; (trozos de la respuesta, en orden). - &#x60;event: done&#x60; con &#x60;data: {\&quot;remainingMessages\&quot;: 27}&#x60; al terminar. - &#x60;event: error&#x60; con &#x60;data: {\&quot;code\&quot;: \&quot;chat_unavailable\&quot;}&#x60; si el proveedor falla a medias. Los errores previos al flujo (sin acceso, sin consentimiento, límite) son problem+json normales con su código HTTP. Requiere vínculo con el paciente (404 si no) y los consentimientos &#x60;health_data&#x60; y &#x60;ai_chat&#x60; del paciente (403 &#x60;consent_required&#x60;): el modelo recibe su plan.  El asistente conoce el plan del paciente (medicamentos, dosis y horarios) sin nombre, documento ni datos de contacto. No cambia dosis ni diagnostica. Sin estado en el servidor: el cliente envía en &#x60;history&#x60; los últimos turnos (máximo 10) y el servidor **no guarda el texto de la conversación**, solo el recuento de mensajes y tokens. Límite diario de mensajes por usuario: al superarlo, 429 con &#x60;code: chat_limit_reached&#x60; y &#x60;Retry-After&#x60;. 
     * Responses:
     *  - 200: Flujo SSE con la respuesta.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 403: Autenticado pero sin permiso: rol que no permite la acción sobre un recurso accesible, o tipo de token equivocado (`wrong_token_type`). Nunca se usa para pacientes sin vínculo (eso es 404). 
     *  - 404: No existe o el usuario no tiene acceso: un cuidador que pide un paciente sin vínculo activo (o sus recursos) recibe 404, igual que si no existiera, para no revelar datos ajenos. 
     *  - 422: La petición no cumple el esquema.
     *  - 429: Límite de peticiones superado.
     *  - 503: El proveedor externo (modelo de IA) no responde (`code: chat_unavailable`). Se puede reintentar; no consume el límite diario. 
     *
     * @param patientId 
     * @param chatRequest 
     * @return [kotlin.String]
     */
    @POST("patients/{patientId}/chat/messages")
    suspend fun sendChatMessage(@Path("patientId") patientId: java.util.UUID, @Body chatRequest: ChatRequest): Response<kotlin.String>

    /**
     * POST devices/me/chat/messages
     * Preguntar al asistente desde el reloj (respuesta corta)
     * Solo con token de reloj (403 con token de usuario). Mismas reglas, guardarraíles y consentimientos (&#x60;health_data&#x60; y &#x60;ai_chat&#x60; del paciente) que el chat web, pero responde JSON sin streaming y con una respuesta corta (como máximo 3 frases, sin listas ni formato) para leerla en voz alta. El límite diario cuenta por reloj. 
     * Responses:
     *  - 200: Respuesta corta.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 403: Autenticado pero sin permiso: rol que no permite la acción sobre un recurso accesible, o tipo de token equivocado (`wrong_token_type`). Nunca se usa para pacientes sin vínculo (eso es 404). 
     *  - 422: La petición no cumple el esquema.
     *  - 429: Límite de peticiones superado.
     *  - 503: El proveedor externo (modelo de IA) no responde (`code: chat_unavailable`). Se puede reintentar; no consume el límite diario. 
     *
     * @param chatRequest 
     * @return [WatchChatReply]
     */
    @POST("devices/me/chat/messages")
    suspend fun sendMyChatMessage(@Body chatRequest: ChatRequest): Response<WatchChatReply>

}
