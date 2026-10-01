package com.smartwatch.recordatorios.data.remote

import com.smartwatch.recordatorios.BuildConfig
import com.smartwatch.recordatorios.api.apis.DevicesApi
import com.smartwatch.recordatorios.api.apis.DosesApi
import com.smartwatch.recordatorios.api.apis.PlanApi
import com.smartwatch.recordatorios.api.infrastructure.ApiClient
import com.smartwatch.recordatorios.api.models.TokenPair
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

/** Solicitud de tokens del reloj (el contrato la define como `oneOf`, que el generador no emite). */
fun deviceCodeGrant(deviceCode: String): JsonObject =
    buildJsonObject {
        put("grantType", "device_code")
        put("deviceCode", deviceCode)
    }

private fun refreshGrant(refreshToken: String): JsonObject =
    buildJsonObject {
        put("grantType", "refresh_token")
        put("refreshToken", refreshToken)
    }

/** Servicios del contrato. Los autenticados renuevan el token al recibir 401. */
@Singleton
class Api
    @Inject
    constructor(
        private val tokens: TokenStore,
    ) {
        private val anonymous = ApiClient(BuildConfig.API_BASE_URL)

        val pairing: DevicesApi = anonymous.createService(DevicesApi::class.java)

        private val authenticated =
            ApiClient(
                BuildConfig.API_BASE_URL,
                OkHttpClient
                    .Builder()
                    .addInterceptor(bearer())
                    .authenticator(refresher()),
            )

        val plan: PlanApi = authenticated.createService(PlanApi::class.java)
        val doses: DosesApi = authenticated.createService(DosesApi::class.java)

        private fun bearer() =
            Interceptor { chain ->
                chain.proceed(chain.request().withToken(tokens.accessToken))
            }

        private fun refresher() =
            object : Authenticator {
                @Synchronized
                override fun authenticate(
                    route: Route?,
                    response: Response,
                ): Request? {
                    val sent = response.request.header("Authorization")?.removePrefix("Bearer ")
                    val current = tokens.accessToken
                    return when {
                        // Otro hilo ya renovó el token mientras esperábamos: basta reintentar.
                        current != null && current != sent -> response.request.withToken(current)
                        response.priorResponse != null -> null
                        else -> refresh()?.let { response.request.withToken(it.accessToken) }
                    }
                }
            }

        private fun refresh(): TokenPair? {
            val refreshToken = tokens.refreshToken ?: return null
            val result =
                runCatching {
                    runBlocking {
                        pairing.requestDeviceToken(
                            refreshGrant(refreshToken),
                        )
                    }
                }.getOrNull()
            val pair = result?.body()
            when {
                pair != null -> tokens.save(pair.accessToken, pair.refreshToken)
                // Refresh inválido, caducado o reloj desvinculado: hay que volver a vincular.
                result != null && result.code() in UNAUTHORIZED_CODES -> tokens.clear()
            }
            return pair
        }

        private fun Request.withToken(token: String?): Request =
            if (token == null) this else newBuilder().header("Authorization", "Bearer $token").build()

        private companion object {
            val UNAUTHORIZED_CODES = setOf(401, 403)
        }
    }
