# MedicationsApi

All URIs are relative to *http://localhost:8000*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**archiveMedication**](MedicationsApi.md#archiveMedication) | **DELETE** patients/{patientId}/medications/{medicationId} | Archivar un medicamento |
| [**createMedication**](MedicationsApi.md#createMedication) | **POST** patients/{patientId}/medications | Crear un medicamento |
| [**createSchedule**](MedicationsApi.md#createSchedule) | **POST** patients/{patientId}/medications/{medicationId}/schedules | Añadir un horario |
| [**deleteSchedule**](MedicationsApi.md#deleteSchedule) | **DELETE** patients/{patientId}/medications/{medicationId}/schedules/{scheduleId} | Eliminar un horario |
| [**getMedication**](MedicationsApi.md#getMedication) | **GET** patients/{patientId}/medications/{medicationId} | Un medicamento |
| [**listMedications**](MedicationsApi.md#listMedications) | **GET** patients/{patientId}/medications | Medicamentos de un paciente |
| [**listSchedules**](MedicationsApi.md#listSchedules) | **GET** patients/{patientId}/medications/{medicationId}/schedules | Horarios de un medicamento |
| [**updateMedication**](MedicationsApi.md#updateMedication) | **PATCH** patients/{patientId}/medications/{medicationId} | Editar un medicamento |
| [**updateSchedule**](MedicationsApi.md#updateSchedule) | **PATCH** patients/{patientId}/medications/{medicationId}/schedules/{scheduleId} | Editar un horario |



Archivar un medicamento

Borrado lógico: deja de aparecer en el plan pero se conserva su historial de tomas. Idempotente. Sube la versión del plan. 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(MedicationsApi::class.java)
val patientId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | 
val medicationId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | 

launch(Dispatchers.IO) {
    webService.archiveMedication(patientId, medicationId)
}
```

### Parameters
| **patientId** | **java.util.UUID**|  | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **medicationId** | **java.util.UUID**|  | |

### Return type

null (empty response body)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/problem+json


Crear un medicamento

Sube la versión del plan del paciente. Mismas reglas de acceso y consentimiento que el listado.

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(MedicationsApi::class.java)
val patientId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | 
val medicationCreate : MedicationCreate =  // MedicationCreate | 

launch(Dispatchers.IO) {
    val result : Medication = webService.createMedication(patientId, medicationCreate)
}
```

### Parameters
| **patientId** | **java.util.UUID**|  | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **medicationCreate** | [**MedicationCreate**](MedicationCreate.md)|  | |

### Return type

[**Medication**](Medication.md)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json, application/problem+json


Añadir un horario

Sube la versión del plan. Máximo 10 horarios por medicamento (422 si se supera).

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(MedicationsApi::class.java)
val patientId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | 
val medicationId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | 
val scheduleCreate : ScheduleCreate =  // ScheduleCreate | 

launch(Dispatchers.IO) {
    val result : Schedule = webService.createSchedule(patientId, medicationId, scheduleCreate)
}
```

### Parameters
| **patientId** | **java.util.UUID**|  | |
| **medicationId** | **java.util.UUID**|  | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **scheduleCreate** | [**ScheduleCreate**](ScheduleCreate.md)|  | |

### Return type

[**Schedule**](Schedule.md)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json, application/problem+json


Eliminar un horario

Idempotente. Sube la versión del plan. Las tomas ya registradas se conservan.

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(MedicationsApi::class.java)
val patientId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | 
val medicationId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | 
val scheduleId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | 

launch(Dispatchers.IO) {
    webService.deleteSchedule(patientId, medicationId, scheduleId)
}
```

### Parameters
| **patientId** | **java.util.UUID**|  | |
| **medicationId** | **java.util.UUID**|  | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **scheduleId** | **java.util.UUID**|  | |

### Return type

null (empty response body)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/problem+json


Un medicamento

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(MedicationsApi::class.java)
val patientId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | 
val medicationId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | 

launch(Dispatchers.IO) {
    val result : Medication = webService.getMedication(patientId, medicationId)
}
```

### Parameters
| **patientId** | **java.util.UUID**|  | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **medicationId** | **java.util.UUID**|  | |

### Return type

[**Medication**](Medication.md)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json


Medicamentos de un paciente

Por defecto solo los activos; &#x60;?includeArchived&#x3D;true&#x60; incluye los archivados. Requiere vínculo activo con el paciente (si no, 404) y consentimiento &#x60;health_data&#x60; del paciente (si no, 403 &#x60;consent_required&#x60;). 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(MedicationsApi::class.java)
val patientId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | 
val cursor : kotlin.String = cursor_example // kotlin.String | Cursor opaco devuelto como `nextCursor` en la página anterior.
val limit : kotlin.Int = 56 // kotlin.Int | 
val includeArchived : kotlin.Boolean = true // kotlin.Boolean | 

launch(Dispatchers.IO) {
    val result : MedicationPage = webService.listMedications(patientId, cursor, limit, includeArchived)
}
```

### Parameters
| **patientId** | **java.util.UUID**|  | |
| **cursor** | **kotlin.String**| Cursor opaco devuelto como &#x60;nextCursor&#x60; en la página anterior. | [optional] |
| **limit** | **kotlin.Int**|  | [optional] [default to 20] |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **includeArchived** | **kotlin.Boolean**|  | [optional] [default to false] |

### Return type

[**MedicationPage**](MedicationPage.md)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json


Horarios de un medicamento

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(MedicationsApi::class.java)
val patientId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | 
val medicationId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | 

launch(Dispatchers.IO) {
    val result : ScheduleList = webService.listSchedules(patientId, medicationId)
}
```

### Parameters
| **patientId** | **java.util.UUID**|  | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **medicationId** | **java.util.UUID**|  | |

### Return type

[**ScheduleList**](ScheduleList.md)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json


Editar un medicamento

Actualización parcial. Sube la versión del plan.

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(MedicationsApi::class.java)
val patientId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | 
val medicationId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | 
val medicationUpdate : MedicationUpdate =  // MedicationUpdate | 

launch(Dispatchers.IO) {
    val result : Medication = webService.updateMedication(patientId, medicationId, medicationUpdate)
}
```

### Parameters
| **patientId** | **java.util.UUID**|  | |
| **medicationId** | **java.util.UUID**|  | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **medicationUpdate** | [**MedicationUpdate**](MedicationUpdate.md)|  | |

### Return type

[**Medication**](Medication.md)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json, application/problem+json


Editar un horario

Actualización parcial. Sube la versión del plan. Cambiar las horas de un horario no reescribe las tomas ya registradas: el historial conserva su &#x60;scheduledAt&#x60; original. 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(MedicationsApi::class.java)
val patientId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | 
val medicationId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | 
val scheduleId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | 
val scheduleUpdate : ScheduleUpdate =  // ScheduleUpdate | 

launch(Dispatchers.IO) {
    val result : Schedule = webService.updateSchedule(patientId, medicationId, scheduleId, scheduleUpdate)
}
```

### Parameters
| **patientId** | **java.util.UUID**|  | |
| **medicationId** | **java.util.UUID**|  | |
| **scheduleId** | **java.util.UUID**|  | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **scheduleUpdate** | [**ScheduleUpdate**](ScheduleUpdate.md)|  | |

### Return type

[**Schedule**](Schedule.md)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json, application/problem+json

