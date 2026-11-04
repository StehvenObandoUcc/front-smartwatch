package com.smartwatch.recordatorios.api.apis

import com.smartwatch.recordatorios.api.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import com.smartwatch.recordatorios.api.models.Consent
import com.smartwatch.recordatorios.api.models.ConsentList
import com.smartwatch.recordatorios.api.models.ConsentPurpose
import com.smartwatch.recordatorios.api.models.ConsentUpdate
import com.smartwatch.recordatorios.api.models.DeleteAccountRequest
import com.smartwatch.recordatorios.api.models.Problem
import com.smartwatch.recordatorios.api.models.User
import com.smartwatch.recordatorios.api.models.UserUpdate
import com.smartwatch.recordatorios.api.models.ValidationProblem

interface UsersApi {
    /**
     * DELETE users/me
     * Eliminar la cuenta
     * Derecho de supresión (Ley 1581 de 2012). Exige la contraseña actual en el cuerpo y es **irreversible**: todo ocurre en una sola transacción. - Se borra la cuenta, sus sesiones, su vinculación de Telegram y sus preferencias. - Si es un paciente con cuenta, se borra su registro de paciente con todo su contenido   (medicamentos, horarios, tomas, reportes, relojes, vínculos y consentimientos). - Si es cuidador, cada paciente gestionado donde es el **único** cuidador activo se borra   con todo su contenido; si hay otros cuidadores activos, el paciente se conserva y solo   desaparece el vínculo de este usuario. - Se borra el uso del chat y los mensajes de notificación aún sin enviar dirigidos a su   correo o a su chat de Telegram. - Los consentimientos que otros conservan (p. ej. los que dio en nombre de un paciente   gestionado que sigue existiendo) se mantienen, con &#x60;grantedBy: null&#x60;. - Los tokens de acceso ya emitidos caducan solos (15 min) y dejan de servir: el usuario   ya no existe (401 &#x60;invalid_token&#x60;). Los refresh tokens y los relojes quedan invalidados. Responde 403 &#x60;invalid_password&#x60; (no 401, para que el cliente web no intente renovar la sesión) si la contraseña no es correcta. Límite de intentos por usuario: 429 con &#x60;Retry-After&#x60;. 
     * Responses:
     *  - 204: Cuenta eliminada.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 403: Autenticado pero sin permiso: rol que no permite la acción sobre un recurso accesible, o tipo de token equivocado (`wrong_token_type`). Nunca se usa para pacientes sin vínculo (eso es 404). 
     *  - 422: La petición no cumple el esquema.
     *  - 429: Límite de peticiones superado.
     *
     * @param deleteAccountRequest 
     * @return [Unit]
     */
    @DELETE("users/me")
    suspend fun deleteMe(@Body deleteAccountRequest: DeleteAccountRequest): Response<Unit>

    /**
     * GET users/me
     * Usuario autenticado
     * 
     * Responses:
     *  - 200: Perfil.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *
     * @return [User]
     */
    @GET("users/me")
    suspend fun getMe(): Response<User>

    /**
     * GET users/me/consents
     * Consentimientos del usuario
     * Consentimientos del usuario autenticado sobre sus propios datos. Para un usuario con rol &#x60;patient&#x60; son los mismos registros que &#x60;GET /patients/{patientId}/consents&#x60; de su perfil. Devuelve siempre las tres finalidades; las no respondidas llevan &#x60;granted &#x3D; false&#x60;. 
     * Responses:
     *  - 200: Estado de cada finalidad.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *
     * @return [ConsentList]
     */
    @GET("users/me/consents")
    suspend fun listMyConsents(): Response<ConsentList>

    /**
     * PUT users/me/consents/{purpose}
     * Otorgar o retirar un consentimiento propio
     * Idempotente. Cada cambio queda registrado (historial) con fecha, versión del texto aceptado y &#x60;grantedBy&#x60; &#x3D; el propio usuario. 
     * Responses:
     *  - 200: Consentimiento actualizado.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 422: La petición no cumple el esquema.
     *
     * @param purpose 
     * @param consentUpdate 
     * @return [Consent]
     */
    @PUT("users/me/consents/{purpose}")
    suspend fun setMyConsent(@Path("purpose") purpose: ConsentPurpose, @Body consentUpdate: ConsentUpdate): Response<Consent>

    /**
     * PATCH users/me
     * Actualizar perfil
     * 
     * Responses:
     *  - 200: Perfil actualizado.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 422: La petición no cumple el esquema.
     *
     * @param userUpdate 
     * @return [User]
     */
    @PATCH("users/me")
    suspend fun updateMe(@Body userUpdate: UserUpdate): Response<User>

}
