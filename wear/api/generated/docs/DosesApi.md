# DosesApi

All URIs are relative to *http://localhost:8000*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**createMyDoseEvents**](DosesApi.md#createMyDoseEvents) | **POST** devices/me/dose-events | Subir eventos de toma (lote, idempotente) |
| [**createPatientDoseEvents**](DosesApi.md#createPatientDoseEvents) | **POST** patients/{patientId}/dose-events | Registrar tomas desde la web (lote, idempotente) |
| [**deleteMyDoseEvent**](DosesApi.md#deleteMyDoseEvent) | **DELETE** devices/me/dose-events/{eventId} | Deshacer una toma desde el reloj |
| [**deletePatientDoseEvent**](DosesApi.md#deletePatientDoseEvent) | **DELETE** patients/{patientId}/dose-events/{eventId} | Deshacer una toma desde la web |
| [**getAdherence**](DosesApi.md#getAdherence) | **GET** patients/{patientId}/adherence | Porcentaje de adherencia de un paciente |
| [**getMyDeviceAdherence**](DosesApi.md#getMyDeviceAdherence) | **GET** devices/me/adherence | Adherencia del paciente del reloj |
| [**listDoseHistory**](DosesApi.md#listDoseHistory) | **GET** patients/{patientId}/dose-history | Historial de dosis de un paciente |



Subir eventos de toma (lote, idempotente)

Solo con token de reloj. Hasta 100 eventos por lote. Idempotente por &#x60;eventId&#x60;: reenviar un evento ya guardado devuelve &#x60;outcome: duplicate&#x60; sin duplicar nada. Cada evento se resuelve por separado y el lote responde 200; un evento inválido no hace fallar a los demás. Un evento solo se acepta si &#x60;scheduleId&#x60; pertenece al paciente del reloj y &#x60;scheduledAt&#x60; es una hora que ese horario generaba; si no, &#x60;outcome: rejected&#x60; con &#x60;code&#x60; (&#x60;schedule_not_found&#x60;, &#x60;invalid_scheduled_at&#x60;). Una dosis tiene como mucho un evento: si llega otro &#x60;eventId&#x60; para la misma dosis (&#x60;scheduleId&#x60; + &#x60;scheduledAt&#x60;), gana el primero y el segundo se rechaza con &#x60;code: dose_already_recorded&#x60;. 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(DosesApi::class.java)
val doseEventBatch : DoseEventBatch =  // DoseEventBatch | 

launch(Dispatchers.IO) {
    val result : DoseEventBatchResult = webService.createMyDoseEvents(doseEventBatch)
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **doseEventBatch** | [**DoseEventBatch**](DoseEventBatch.md)|  | |

### Return type

[**DoseEventBatchResult**](DoseEventBatchResult.md)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json, application/problem+json


Registrar tomas desde la web (lote, idempotente)

Para el paciente con cuenta (su propio perfil) o un cuidador con vínculo activo, cuando el paciente no tiene reloj a mano o se olvidó de marcar. Mismas reglas, cuerpo y respuesta que &#x60;POST /devices/me/dose-events&#x60;: hasta 100 eventos, idempotente por &#x60;eventId&#x60; (lo genera el cliente), cada evento se resuelve por separado y el lote responde 200, y una dosis tiene como mucho un evento (&#x60;dose_already_recorded&#x60;). Requiere el consentimiento &#x60;health_data&#x60; (403 &#x60;consent_required&#x60;). Un paciente sin vínculo con el usuario es 404. Los eventos guardados quedan sin dispositivo asociado. 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(DosesApi::class.java)
val patientId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | 
val doseEventBatch : DoseEventBatch =  // DoseEventBatch | 

launch(Dispatchers.IO) {
    val result : DoseEventBatchResult = webService.createPatientDoseEvents(patientId, doseEventBatch)
}
```

### Parameters
| **patientId** | **java.util.UUID**|  | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **doseEventBatch** | [**DoseEventBatch**](DoseEventBatch.md)|  | |

### Return type

[**DoseEventBatchResult**](DoseEventBatchResult.md)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json, application/problem+json


Deshacer una toma desde el reloj

Solo con token de reloj (403 con token de usuario) y solo eventos del paciente del reloj, los haya registrado el reloj o la web. Borra el evento de una dosis: la dosis vuelve a estar pendiente (o &#x60;MISSED&#x60; si ya pasó su hora), sale del historial y de la adherencia, y la dosis admite un evento nuevo. Para corregir (por ejemplo, de &#x60;TAKEN&#x60; a &#x60;SKIPPED&#x60;) se deshace y se registra otro con un &#x60;eventId&#x60; nuevo. Idempotente: un &#x60;eventId&#x60; que no existe o ya se deshizo responde 204. Un &#x60;eventId&#x60; deshecho puede volver a enviarse en un lote. Requiere el consentimiento &#x60;health_data&#x60; (403 &#x60;consent_required&#x60;). 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(DosesApi::class.java)
val eventId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | `eventId` del evento de toma (lo generó el cliente al registrarlo).

launch(Dispatchers.IO) {
    webService.deleteMyDoseEvent(eventId)
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **eventId** | **java.util.UUID**| &#x60;eventId&#x60; del evento de toma (lo generó el cliente al registrarlo). | |

### Return type

null (empty response body)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/problem+json


Deshacer una toma desde la web

Para el paciente con cuenta o un cuidador con vínculo activo; un paciente sin vínculo es 404. Sirve para eventos del reloj o de la web (el &#x60;eventId&#x60; está en el historial). Borra el evento de una dosis: la dosis vuelve a estar pendiente (o &#x60;MISSED&#x60; si ya pasó su hora), sale del historial y de la adherencia, y la dosis admite un evento nuevo. Para corregir (por ejemplo, de &#x60;TAKEN&#x60; a &#x60;SKIPPED&#x60;) se deshace y se registra otro con un &#x60;eventId&#x60; nuevo. Idempotente: un &#x60;eventId&#x60; que no existe o ya se deshizo responde 204. Un &#x60;eventId&#x60; deshecho puede volver a enviarse en un lote. Requiere el consentimiento &#x60;health_data&#x60; (403 &#x60;consent_required&#x60;). 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(DosesApi::class.java)
val patientId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | 
val eventId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | `eventId` del evento de toma (lo generó el cliente al registrarlo).

launch(Dispatchers.IO) {
    webService.deletePatientDoseEvent(patientId, eventId)
}
```

### Parameters
| **patientId** | **java.util.UUID**|  | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **eventId** | **java.util.UUID**| &#x60;eventId&#x60; del evento de toma (lo generó el cliente al registrarlo). | |

### Return type

null (empty response body)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/problem+json


Porcentaje de adherencia de un paciente

&#x60;percentage &#x3D; taken / (taken + skipped + missed) * 100&#x60; sobre las dosis vencidas del rango (mismas reglas y rango por defecto que el historial). &#x60;null&#x60; si no hubo dosis vencidas. 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(DosesApi::class.java)
val patientId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | 
val from : java.time.LocalDate = 2013-10-20 // java.time.LocalDate | Inicio del rango (inclusive), fecha local del paciente.
val to : java.time.LocalDate = 2013-10-20 // java.time.LocalDate | Fin del rango (inclusive), fecha local del paciente.

launch(Dispatchers.IO) {
    val result : Adherence = webService.getAdherence(patientId, from, to)
}
```

### Parameters
| **patientId** | **java.util.UUID**|  | |
| **from** | **java.time.LocalDate**| Inicio del rango (inclusive), fecha local del paciente. | [optional] |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **to** | **java.time.LocalDate**| Fin del rango (inclusive), fecha local del paciente. | [optional] |

### Return type

[**Adherence**](Adherence.md)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json


Adherencia del paciente del reloj

Solo con token de reloj (403 con token de usuario). El paciente es el del reloj vinculado. Mismas reglas que &#x60;GET /patients/{patientId}/adherence&#x60;: &#x60;percentage&#x60; &#x3D; taken / (taken + skipped + missed) * 100 sobre las dosis vencidas, o null si no hubo ninguna. Rango por defecto: los últimos 14 días en la zona horaria del paciente; máximo 90 días. Requiere el consentimiento &#x60;health_data&#x60; (403 &#x60;consent_required&#x60;). 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(DosesApi::class.java)
val from : java.time.LocalDate = 2013-10-20 // java.time.LocalDate | Inicio del rango (inclusive), fecha local del paciente.
val to : java.time.LocalDate = 2013-10-20 // java.time.LocalDate | Fin del rango (inclusive), fecha local del paciente.

launch(Dispatchers.IO) {
    val result : DeviceAdherence = webService.getMyDeviceAdherence(from, to)
}
```

### Parameters
| **from** | **java.time.LocalDate**| Inicio del rango (inclusive), fecha local del paciente. | [optional] |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **to** | **java.time.LocalDate**| Fin del rango (inclusive), fecha local del paciente. | [optional] |

### Return type

[**DeviceAdherence**](DeviceAdherence.md)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json


Historial de dosis de un paciente

Dosis ya vencidas, de la más reciente a la más antigua, con su estado. Una dosis sin evento cuya hora programada pasó hace más de 60 minutos se informa como &#x60;MISSED&#x60; (se calcula al consultar). Las dosis sin evento futuras o dentro de la ventana de 60 min no aparecen; una dosis con evento aparece siempre. Solo cuentan las dosis que el horario ya generaba cuando se creó o editó, y mientras el medicamento no estaba archivado. Rango por defecto: últimos 7 días; máximo 90 días, y &#x60;from&#x60; no puede ser posterior a &#x60;to&#x60; (422). 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(DosesApi::class.java)
val patientId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | 
val cursor : kotlin.String = cursor_example // kotlin.String | Cursor opaco devuelto como `nextCursor` en la página anterior.
val limit : kotlin.Int = 56 // kotlin.Int | 
val from : java.time.LocalDate = 2013-10-20 // java.time.LocalDate | Inicio del rango (inclusive), fecha local del paciente.
val to : java.time.LocalDate = 2013-10-20 // java.time.LocalDate | Fin del rango (inclusive), fecha local del paciente.

launch(Dispatchers.IO) {
    val result : DoseHistoryPage = webService.listDoseHistory(patientId, cursor, limit, from, to)
}
```

### Parameters
| **patientId** | **java.util.UUID**|  | |
| **cursor** | **kotlin.String**| Cursor opaco devuelto como &#x60;nextCursor&#x60; en la página anterior. | [optional] |
| **limit** | **kotlin.Int**|  | [optional] [default to 20] |
| **from** | **java.time.LocalDate**| Inicio del rango (inclusive), fecha local del paciente. | [optional] |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **to** | **java.time.LocalDate**| Fin del rango (inclusive), fecha local del paciente. | [optional] |

### Return type

[**DoseHistoryPage**](DoseHistoryPage.md)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json

