package com.smartwatch.recordatorios.api.apis

import com.smartwatch.recordatorios.api.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import com.smartwatch.recordatorios.api.models.AccessToken
import com.smartwatch.recordatorios.api.models.AuthSession
import com.smartwatch.recordatorios.api.models.ForgotPasswordRequest
import com.smartwatch.recordatorios.api.models.LoginRequest
import com.smartwatch.recordatorios.api.models.Problem
import com.smartwatch.recordatorios.api.models.RegisterRequest
import com.smartwatch.recordatorios.api.models.ResetPasswordRequest
import com.smartwatch.recordatorios.api.models.TokenBody
import com.smartwatch.recordatorios.api.models.ValidationProblem

interface AuthApi {
    /**
     * POST auth/forgot-password
     * Pedir el correo de recuperación de contraseña
     * Responde siempre 202, exista o no el correo, para no revelar qué cuentas existen. Si existe y está verificado, se envía un enlace al panel (&#x60;{WEB_ORIGIN}/reset-password?token&#x3D;...&#x60;) con un token de un solo uso que caduca a la hora. Límite de peticiones por IP y por correo (429). 
     * Responses:
     *  - 202: Si la cuenta existe, recibirá un correo.
     *  - 422: La petición no cumple el esquema.
     *  - 429: Límite de peticiones superado.
     *
     * @param forgotPasswordRequest 
     * @return [Unit]
     */
    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body forgotPasswordRequest: ForgotPasswordRequest): Response<Unit>

    /**
     * POST auth/login
     * Iniciar sesión
     * Abre sesión web: token de acceso en el JSON y refresh token en la cookie &#x60;__Secure-refresh-token&#x60;. Credenciales incorrectas: 401 (&#x60;code: invalid_credentials&#x60;), sin distinguir si el correo existe. Límite de peticiones por IP y por correo (429). 
     * Responses:
     *  - 200: Sesión abierta.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 422: La petición no cumple el esquema.
     *  - 429: Límite de peticiones superado.
     *
     * @param loginRequest 
     * @return [AuthSession]
     */
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<AuthSession>

    /**
     * POST auth/logout
     * Cerrar sesión web
     * Sin cuerpo y sin token de acceso (puede haber caducado). Revoca la familia del refresh token de la cookie, si la hay, y borra la cookie. Idempotente: sin cookie también responde 204. 
     * Responses:
     *  - 204: Sesión cerrada.
     *
     * @param secureRefreshToken Refresh token de la sesión web. Lo envía el navegador; JavaScript no puede leerlo. (optional)
     * @return [Unit]
     */
    @POST("auth/logout")
    suspend fun logout(): Response<Unit>

    /**
     * POST auth/refresh
     * Renovar la sesión web
     * Sin cuerpo. Lee el refresh token de la cookie &#x60;__Secure-refresh-token&#x60;, lo rota y devuelve un token de acceso nuevo en el JSON y el refresh token nuevo en la cookie. - Sin cookie, o refresh token inválido o caducado: 401 y la cookie se borra. - Reutilizar un refresh token ya rotado revoca toda la familia (401,   &#x60;code: refresh_token_reused&#x60;) y la cookie se borra. - Un refresh token de reloj aquí da 401; los relojes renuevan con &#x60;POST /devices/token&#x60;. 
     * Responses:
     *  - 200: Sesión renovada.
     *  - 401: Sin sesión válida. La respuesta borra la cookie.
     *
     * @param secureRefreshToken Refresh token de la sesión web. Lo envía el navegador; JavaScript no puede leerlo. (optional)
     * @return [AccessToken]
     */
    @POST("auth/refresh")
    suspend fun refreshTokens(): Response<AccessToken>

    /**
     * POST auth/register
     * Crear cuenta
     * Crea un usuario y abre sesión web. Si &#x60;role&#x60; es &#x60;patient&#x60;, en la misma transacción se crea su registro en &#x60;/patients&#x60; (&#x60;managed: false&#x60;, con el &#x60;displayName&#x60; y &#x60;timezone&#x60; del registro) y &#x60;user.patientId&#x60; apunta a él. Si &#x60;role&#x60; es &#x60;caregiver&#x60;, &#x60;user.patientId&#x60; es null. Devuelve el token de acceso en el JSON y el refresh token en la cookie &#x60;__Secure-refresh-token&#x60;. Límite de peticiones por IP (429). Encola un correo de verificación (&#x60;POST /auth/verify-email&#x60;); no hace falta verificarlo para usar la cuenta. 
     * Responses:
     *  - 201: Cuenta creada y sesión abierta.
     *  - 409: Conflicto con el estado actual (por ejemplo, correo ya registrado).
     *  - 422: La petición no cumple el esquema.
     *  - 429: Límite de peticiones superado.
     *
     * @param registerRequest 
     * @return [AuthSession]
     */
    @POST("auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<AuthSession>

    /**
     * POST auth/resend-verification
     * Reenviar el correo de verificación
     * Solo con sesión de usuario. Invalida el token anterior. Si el correo ya está verificado devuelve 204 sin enviar nada. Límite de peticiones por usuario (429). 
     * Responses:
     *  - 202: Correo encolado para envío.
     *  - 204: El correo ya estaba verificado.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 403: Autenticado pero sin permiso: rol que no permite la acción sobre un recurso accesible, o tipo de token equivocado (`wrong_token_type`). Nunca se usa para pacientes sin vínculo (eso es 404). 
     *  - 429: Límite de peticiones superado.
     *
     * @return [Unit]
     */
    @POST("auth/resend-verification")
    suspend fun resendVerification(): Response<Unit>

    /**
     * POST auth/reset-password
     * Fijar una contraseña nueva con el token de recuperación
     * Token inválido, caducado o ya usado: 400 (&#x60;code: invalid_token&#x60;). Al cambiar la contraseña se revocan todos los refresh tokens del usuario (web); los relojes vinculados no se tocan. No abre sesión: la web lleva al usuario a iniciarla. 
     * Responses:
     *  - 204: Contraseña cambiada.
     *  - 400: Token de un solo uso inválido, caducado o ya usado (`code: invalid_token`).
     *  - 422: La petición no cumple el esquema.
     *  - 429: Límite de peticiones superado.
     *
     * @param resetPasswordRequest 
     * @return [Unit]
     */
    @POST("auth/reset-password")
    suspend fun resetPassword(@Body resetPasswordRequest: ResetPasswordRequest): Response<Unit>

    /**
     * POST auth/verify-email
     * Verificar el correo con el token recibido
     * El token llega por correo en un enlace al panel (&#x60;{WEB_ORIGIN}/verify-email?token&#x3D;...&#x60;), es de un solo uso y caduca a las 24 horas. Token inválido, caducado o ya usado: 400 (&#x60;code: invalid_token&#x60;). Verificar un correo ya verificado con un token válido devuelve 204. 
     * Responses:
     *  - 204: Correo verificado.
     *  - 400: Token de un solo uso inválido, caducado o ya usado (`code: invalid_token`).
     *  - 422: La petición no cumple el esquema.
     *  - 429: Límite de peticiones superado.
     *
     * @param tokenBody 
     * @return [Unit]
     */
    @POST("auth/verify-email")
    suspend fun verifyEmail(@Body tokenBody: TokenBody): Response<Unit>

}
