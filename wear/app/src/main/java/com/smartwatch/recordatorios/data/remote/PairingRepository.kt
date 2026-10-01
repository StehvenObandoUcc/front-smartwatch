package com.smartwatch.recordatorios.data.remote

import android.os.Build
import com.smartwatch.recordatorios.BuildConfig
import com.smartwatch.recordatorios.api.apis.DevicesApi
import com.smartwatch.recordatorios.api.models.PairingCode
import com.smartwatch.recordatorios.api.models.PairingCodeRequest
import com.smartwatch.recordatorios.api.models.TokenPair
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/** Vinculación sin teclado (RFC 8628): el reloj muestra el código y consulta hasta que la web lo confirma. */
@Singleton
class PairingRepository
    @Inject
    constructor(
        private val api: DevicesApi,
        private val tokens: TokenStore,
        private val plan: PlanRepository,
    ) {
        suspend fun requestCode(): PairingCode? =
            try {
                api.createPairingCode(PairingCodeRequest(Build.MODEL, BuildConfig.VERSION_NAME)).body()
            } catch (_: IOException) {
                null
            }

        /** Consulta hasta recibir los tokens. false si el código caducó o falló: hay que pedir otro. */
        suspend fun awaitConfirmation(code: PairingCode): Boolean {
            var intervalSeconds = code.interval.toLong()
            val deadline = System.currentTimeMillis() + code.expiresIn * MILLIS
            var paired = false
            var failed = false
            while (!paired && !failed && System.currentTimeMillis() < deadline) {
                delay(intervalSeconds * MILLIS)
                val response = pollToken(code)
                val pair = response?.body()
                when {
                    pair != null -> {
                        plan.reset()
                        tokens.save(pair.accessToken, pair.refreshToken)
                        paired = true
                    }
                    response == null -> Unit
                    else ->
                        when (errorCode(response.errorBody()?.string())) {
                            "slow_down" -> intervalSeconds += SLOW_DOWN_SECONDS
                            "authorization_pending", null -> Unit
                            else -> failed = true
                        }
                }
            }
            return paired
        }

        private suspend fun pollToken(code: PairingCode): Response<TokenPair>? =
            try {
                api.requestDeviceToken(deviceCodeGrant(code.deviceCode))
            } catch (_: IOException) {
                null
            }

        private fun errorCode(body: String?): String? =
            runCatching {
                Json
                    .parseToJsonElement(
                        body.orEmpty(),
                    ).jsonObject["code"]
                    ?.jsonPrimitive
                    ?.contentOrNull
            }.getOrNull()

        private companion object {
            const val MILLIS = 1_000L
            const val SLOW_DOWN_SECONDS = 5L
        }
    }
