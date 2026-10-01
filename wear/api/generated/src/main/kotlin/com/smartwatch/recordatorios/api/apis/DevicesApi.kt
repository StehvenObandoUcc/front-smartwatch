package com.smartwatch.recordatorios.api.apis

import com.smartwatch.recordatorios.api.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import com.smartwatch.recordatorios.api.models.Device
import com.smartwatch.recordatorios.api.models.DevicePage
import com.smartwatch.recordatorios.api.models.PairingCode
import com.smartwatch.recordatorios.api.models.PairingCodeRequest
import com.smartwatch.recordatorios.api.models.PairingConfirm
import com.smartwatch.recordatorios.api.models.Problem
import com.smartwatch.recordatorios.api.models.PushTokenUpdate
import com.smartwatch.recordatorios.api.models.TokenPair
import com.smartwatch.recordatorios.api.models.ValidationProblem

interface DevicesApi {
    /**
     * POST devices/pairing-codes/{code}/confirm
     * Confirmar la vinculación desde la web
     * Paso 2. Desde la web, el usuario escribe el código que muestra el reloj y envía en el cuerpo el &#x60;patientId&#x60; al que se vinculará. Puede hacerlo el paciente con cuenta (sobre sí mismo) o un cuidador con vínculo activo. - Código inexistente, caducado o ya confirmado: 404 (&#x60;code: pairing_code_not_found&#x60;). - &#x60;patientId&#x60; sin vínculo activo con el usuario: 404 (&#x60;code: patient_not_found&#x60;). - Límite de intentos por usuario e IP (429) para impedir adivinar códigos. Un paciente solo tiene un reloj activo: si ya había otro, se desvincula y se revocan sus tokens. 
     * Responses:
     *  - 200: Reloj vinculado.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 403: Autenticado pero sin permiso: rol que no permite la acción sobre un recurso accesible, o tipo de token equivocado (`wrong_token_type`). Nunca se usa para pacientes sin vínculo (eso es 404). 
     *  - 404: No existe o el usuario no tiene acceso: un cuidador que pide un paciente sin vínculo activo (o sus recursos) recibe 404, igual que si no existiera, para no revelar datos ajenos. 
     *  - 422: La petición no cumple el esquema.
     *  - 429: Límite de peticiones superado.
     *
     * @param code 
     * @param pairingConfirm 
     * @return [Device]
     */
    @POST("devices/pairing-codes/{code}/confirm")
    suspend fun confirmPairingCode(@Path("code") code: kotlin.String, @Body pairingConfirm: PairingConfirm): Response<Device>

    /**
     * POST devices/pairing-codes
     * El reloj pide un código de vinculación
     * Paso 1 del flujo tipo device code (RFC 8628). El reloj muestra &#x60;code&#x60; en pantalla y guarda &#x60;deviceCode&#x60; en secreto para consultar &#x60;POST /devices/token&#x60; cada &#x60;interval&#x60; segundos. Ambos son de un solo uso y caducan a los &#x60;expiresIn&#x60; segundos (10 min). Límite de peticiones por IP (429). 
     * Responses:
     *  - 201: Código creado.
     *  - 422: La petición no cumple el esquema.
     *  - 429: Límite de peticiones superado.
     *
     * @param pairingCodeRequest 
     * @return [PairingCode]
     */
    @POST("devices/pairing-codes")
    suspend fun createPairingCode(@Body pairingCodeRequest: PairingCodeRequest): Response<PairingCode>

    /**
     * GET devices/me
     * El reloj consulta su vinculación
     * Solo con token de reloj; un token de usuario recibe 403.
     * Responses:
     *  - 200: Reloj y paciente vinculado.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 403: Autenticado pero sin permiso: rol que no permite la acción sobre un recurso accesible, o tipo de token equivocado (`wrong_token_type`). Nunca se usa para pacientes sin vínculo (eso es 404). 
     *
     * @return [Device]
     */
    @GET("devices/me")
    suspend fun getMyDevice(): Response<Device>

    /**
     * GET patients/{patientId}/devices
     * Relojes de un paciente
     * 
     * Responses:
     *  - 200: Página de relojes activos.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 404: No existe o el usuario no tiene acceso: un cuidador que pide un paciente sin vínculo activo (o sus recursos) recibe 404, igual que si no existiera, para no revelar datos ajenos. 
     *  - 422: La petición no cumple el esquema.
     *
     * @param patientId 
     * @param cursor Cursor opaco devuelto como &#x60;nextCursor&#x60; en la página anterior. (optional)
     * @param limit  (optional, default to 20)
     * @return [DevicePage]
     */
    @GET("patients/{patientId}/devices")
    suspend fun listPatientDevices(@Path("patientId") patientId: java.util.UUID, @Query("cursor") cursor: kotlin.String? = null, @Query("limit") limit: kotlin.Int? = 20): Response<DevicePage>

    /**
     * POST devices/token
     * El reloj obtiene o renueva sus tokens
     * Único endpoint de tokens del reloj, según &#x60;grantType&#x60;:  **&#x60;device_code&#x60;** (paso 3 de la vinculación). Mientras la web no confirme responde 400 con &#x60;code&#x60;: - &#x60;authorization_pending&#x60;: sigue esperando. - &#x60;slow_down&#x60;: consulta demasiado rápido; suma 5 s a &#x60;interval&#x60;. - &#x60;expired_token&#x60;: el código caducó o ya se usó; hay que pedir otro. Tras la confirmación devuelve los tokens una sola vez y el &#x60;deviceCode&#x60; queda consumido.  **&#x60;refresh_token&#x60;** (renovación). El reloj renueva su token de acceso antes de que caduque o al recibir 401. El refresh token rota en cada uso; reutilizar uno ya rotado revoca los tokens del reloj (&#x60;code: refresh_token_reused&#x60;) y hay que volver a vincularlo. Un refresh token inválido, caducado (90 días sin uso) o de un reloj desvinculado responde 401. Un refresh token de usuario aquí también responde 401.  Límite de peticiones por IP y por &#x60;deviceCode&#x60; (429). 
     * Responses:
     *  - 200: Reloj vinculado; tokens del dispositivo.
     *  - 400: Vinculación aún no completada o código caducado.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 422: La petición no cumple el esquema.
     *  - 429: Límite de peticiones superado.
     *
     * @param kotlinxSerializationJsonJsonObject 
     * @return [TokenPair]
     */
    @POST("devices/token")
    suspend fun requestDeviceToken(@Body kotlinxSerializationJsonJsonObject: kotlinx.serialization.json.JsonObject): Response<TokenPair>

    /**
     * PUT devices/me/push-token
     * Registrar el token FCM del reloj
     * Solo con token de reloj; un token de usuario recibe 403. Idempotente; reemplaza el anterior.
     * Responses:
     *  - 204: Token guardado.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 403: Autenticado pero sin permiso: rol que no permite la acción sobre un recurso accesible, o tipo de token equivocado (`wrong_token_type`). Nunca se usa para pacientes sin vínculo (eso es 404). 
     *  - 422: La petición no cumple el esquema.
     *
     * @param pushTokenUpdate 
     * @return [Unit]
     */
    @PUT("devices/me/push-token")
    suspend fun setMyPushToken(@Body pushTokenUpdate: PushTokenUpdate): Response<Unit>

    /**
     * DELETE devices/{deviceId}
     * Desvincular un reloj
     * Lo hacen el paciente con cuenta o un cuidador con vínculo activo. Revoca los tokens del reloj de inmediato. Un reloj de un paciente sin vínculo con el usuario responde 404. Idempotente: desvincular un reloj ya desvinculado devuelve 204. 
     * Responses:
     *  - 204: Reloj desvinculado.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 404: No existe o el usuario no tiene acceso: un cuidador que pide un paciente sin vínculo activo (o sus recursos) recibe 404, igual que si no existiera, para no revelar datos ajenos. 
     *
     * @param deviceId 
     * @return [Unit]
     */
    @DELETE("devices/{deviceId}")
    suspend fun unpairDevice(@Path("deviceId") deviceId: java.util.UUID): Response<Unit>

}
