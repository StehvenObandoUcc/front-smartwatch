package com.smartwatch.recordatorios.api.apis

import com.smartwatch.recordatorios.api.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import com.smartwatch.recordatorios.api.models.NotificationChannelList
import com.smartwatch.recordatorios.api.models.NotificationPreferences
import com.smartwatch.recordatorios.api.models.Problem
import com.smartwatch.recordatorios.api.models.TelegramLink
import com.smartwatch.recordatorios.api.models.ValidationProblem

interface NotificationsApi {
    /**
     * POST users/me/notification-channels/telegram/link
     * Pedir el enlace para vincular Telegram
     * Devuelve &#x60;https://t.me/&lt;Bot&gt;?start&#x3D;&lt;token&gt;&#x60;. El token es de un solo uso y caduca a los 15 minutos; pedir otro invalida el anterior. Al abrir el enlace y pulsar \&quot;Iniciar\&quot;, el bot recibe el token por el webhook y vincula el chat con el usuario; el bot solo puede escribir a quien lo inició. La web consulta &#x60;GET /users/me/notification-channels&#x60; hasta ver &#x60;telegram.linked: true&#x60;. Límite de peticiones por usuario (429). 
     * Responses:
     *  - 201: Enlace creado.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 403: Autenticado pero sin permiso: rol que no permite la acción sobre un recurso accesible, o tipo de token equivocado (`wrong_token_type`). Nunca se usa para pacientes sin vínculo (eso es 404). 
     *  - 429: Límite de peticiones superado.
     *  - 503: El bot no está configurado en el servidor (`code: telegram_not_configured`).
     *
     * @return [TelegramLink]
     */
    @POST("users/me/notification-channels/telegram/link")
    suspend fun createTelegramLink(): Response<TelegramLink>

    /**
     * GET users/me/notification-preferences
     * Preferencias de notificación
     * Qué avisos llegan por qué canal. Por defecto todo activado; un aviso solo se envía si el canal está vinculado (y el correo verificado) y el usuario concedió el consentimiento &#x60;notifications&#x60; (el del paciente, si el usuario es un paciente con cuenta). 
     * Responses:
     *  - 200: Preferencias.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 403: Autenticado pero sin permiso: rol que no permite la acción sobre un recurso accesible, o tipo de token equivocado (`wrong_token_type`). Nunca se usa para pacientes sin vínculo (eso es 404). 
     *
     * @return [NotificationPreferences]
     */
    @GET("users/me/notification-preferences")
    suspend fun getMyNotificationPreferences(): Response<NotificationPreferences>

    /**
     * GET users/me/notification-channels
     * Canales de notificación del usuario
     * Siempre devuelve los dos canales. &#x60;email&#x60; es el correo de la cuenta (&#x60;linked&#x60; siempre true; &#x60;verified&#x60; indica si se verificó). &#x60;telegram&#x60; está vinculado cuando el usuario abrió el enlace de &#x60;POST /users/me/notification-channels/telegram/link&#x60;. 
     * Responses:
     *  - 200: Canales.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 403: Autenticado pero sin permiso: rol que no permite la acción sobre un recurso accesible, o tipo de token equivocado (`wrong_token_type`). Nunca se usa para pacientes sin vínculo (eso es 404). 
     *
     * @return [NotificationChannelList]
     */
    @GET("users/me/notification-channels")
    suspend fun listMyNotificationChannels(): Response<NotificationChannelList>

    /**
     * PUT users/me/notification-preferences
     * Guardar las preferencias de notificación
     * Reemplazo completo e idempotente.
     * Responses:
     *  - 200: Preferencias guardadas.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 403: Autenticado pero sin permiso: rol que no permite la acción sobre un recurso accesible, o tipo de token equivocado (`wrong_token_type`). Nunca se usa para pacientes sin vínculo (eso es 404). 
     *  - 422: La petición no cumple el esquema.
     *
     * @param notificationPreferences 
     * @return [NotificationPreferences]
     */
    @PUT("users/me/notification-preferences")
    suspend fun setMyNotificationPreferences(@Body notificationPreferences: NotificationPreferences): Response<NotificationPreferences>

    /**
     * DELETE users/me/notification-channels/telegram
     * Desvincular Telegram
     * Idempotente: si no estaba vinculado devuelve 204.
     * Responses:
     *  - 204: Telegram desvinculado.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 403: Autenticado pero sin permiso: rol que no permite la acción sobre un recurso accesible, o tipo de token equivocado (`wrong_token_type`). Nunca se usa para pacientes sin vínculo (eso es 404). 
     *
     * @return [Unit]
     */
    @DELETE("users/me/notification-channels/telegram")
    suspend fun unlinkTelegram(): Response<Unit>

}
