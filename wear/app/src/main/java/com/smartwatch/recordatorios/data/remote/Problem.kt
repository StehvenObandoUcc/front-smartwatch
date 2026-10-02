package com.smartwatch.recordatorios.data.remote

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import retrofit2.Response

/** `code` del problem+json (RFC 9457) de una respuesta de error, si lo trae. */
fun <T> Response<T>.problemCode(): String? =
    runCatching {
        Json
            .parseToJsonElement(errorBody()?.string().orEmpty())
            .jsonObject["code"]
            ?.jsonPrimitive
            ?.contentOrNull
    }.getOrNull()
