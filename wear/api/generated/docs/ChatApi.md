# ChatApi

All URIs are relative to *http://localhost:8000*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**sendChatMessage**](ChatApi.md#sendChatMessage) | **POST** patients/{patientId}/chat/messages | Preguntar al asistente sobre el plan de un paciente (web, SSE) |
| [**sendMyChatMessage**](ChatApi.md#sendMyChatMessage) | **POST** devices/me/chat/messages | Preguntar al asistente desde el reloj (respuesta corta) |



Preguntar al asistente sobre el plan de un paciente (web, SSE)

La respuesta llega como flujo &#x60;text/event-stream&#x60; (SSE) con tres tipos de evento: - &#x60;event: delta&#x60; con &#x60;data: {\&quot;text\&quot;: \&quot;...\&quot;}&#x60; (trozos de la respuesta, en orden). - &#x60;event: done&#x60; con &#x60;data: {\&quot;remainingMessages\&quot;: 27}&#x60; al terminar. - &#x60;event: error&#x60; con &#x60;data: {\&quot;code\&quot;: \&quot;chat_unavailable\&quot;}&#x60; si el proveedor falla a medias. Los errores previos al flujo (sin acceso, sin consentimiento, límite) son problem+json normales con su código HTTP. Requiere vínculo con el paciente (404 si no) y los consentimientos &#x60;health_data&#x60; y &#x60;ai_chat&#x60; del paciente (403 &#x60;consent_required&#x60;): el modelo recibe su plan.  El asistente conoce el plan del paciente (medicamentos, dosis y horarios) sin nombre, documento ni datos de contacto. No cambia dosis ni diagnostica. Sin estado en el servidor: el cliente envía en &#x60;history&#x60; los últimos turnos (máximo 10) y el servidor **no guarda el texto de la conversación**, solo el recuento de mensajes y tokens. Límite diario de mensajes por usuario: al superarlo, 429 con &#x60;code: chat_limit_reached&#x60; y &#x60;Retry-After&#x60;. 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(ChatApi::class.java)
val patientId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | 
val chatRequest : ChatRequest =  // ChatRequest | 

launch(Dispatchers.IO) {
    val result : kotlin.String = webService.sendChatMessage(patientId, chatRequest)
}
```

### Parameters
| **patientId** | **java.util.UUID**|  | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **chatRequest** | [**ChatRequest**](ChatRequest.md)|  | |

### Return type

**kotlin.String**

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: text/event-stream, application/problem+json


Preguntar al asistente desde el reloj (respuesta corta)

Solo con token de reloj (403 con token de usuario). Mismas reglas, guardarraíles y consentimientos (&#x60;health_data&#x60; y &#x60;ai_chat&#x60; del paciente) que el chat web, pero responde JSON sin streaming y con una respuesta corta (como máximo 3 frases, sin listas ni formato) para leerla en voz alta. El límite diario cuenta por reloj. 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(ChatApi::class.java)
val chatRequest : ChatRequest =  // ChatRequest | 

launch(Dispatchers.IO) {
    val result : WatchChatReply = webService.sendMyChatMessage(chatRequest)
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **chatRequest** | [**ChatRequest**](ChatRequest.md)|  | |

### Return type

[**WatchChatReply**](WatchChatReply.md)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json, application/problem+json

