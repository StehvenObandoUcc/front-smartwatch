# PlanApi

All URIs are relative to *http://localhost:8000*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**getMyPlan**](PlanApi.md#getMyPlan) | **GET** devices/me/plan | Plan de 7 días del reloj |
| [**getPatientPlan**](PlanApi.md#getPatientPlan) | **GET** patients/{patientId}/plan | Plan de 7 días de un paciente (agenda web) |



Plan de 7 días del reloj

Solo con token de reloj (403 con token de usuario). Devuelve las dosis de los próximos 7 días, desde el inicio del día local del paciente, calculadas con su zona horaria. &#x60;version&#x60; sube con cada cambio de medicamentos u horarios del paciente. El &#x60;ETag&#x60; cambia cuando cambia la versión o el día local; con &#x60;If-None-Match&#x60; igual responde 304 sin cuerpo. Si el paciente no ha dado el consentimiento &#x60;health_data&#x60;, 403 (&#x60;code: consent_required&#x60;). 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(PlanApi::class.java)
val ifNoneMatch : kotlin.String = ifNoneMatch_example // kotlin.String | 

launch(Dispatchers.IO) {
    val result : Plan = webService.getMyPlan(ifNoneMatch)
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **ifNoneMatch** | **kotlin.String**|  | [optional] |

### Return type

[**Plan**](Plan.md)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json


Plan de 7 días de un paciente (agenda web)

Mismo cuerpo que &#x60;GET /devices/me/plan&#x60;, sin ETag. Para la agenda de hoy y de la semana.

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(PlanApi::class.java)
val patientId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | 

launch(Dispatchers.IO) {
    val result : Plan = webService.getPatientPlan(patientId)
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **patientId** | **java.util.UUID**|  | |

### Return type

[**Plan**](Plan.md)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json

