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
import com.smartwatch.recordatorios.api.models.Problem
import com.smartwatch.recordatorios.api.models.User
import com.smartwatch.recordatorios.api.models.UserUpdate
import com.smartwatch.recordatorios.api.models.ValidationProblem

interface UsersApi {
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
