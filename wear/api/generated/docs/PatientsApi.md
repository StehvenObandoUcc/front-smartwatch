# PatientsApi

All URIs are relative to *http://localhost:8000*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**acceptCaregiverInvitation**](PatientsApi.md#acceptCaregiverInvitation) | **POST** caregiver-invitations/{code}/accept | Aceptar una invitación |
| [**createCaregiverInvitation**](PatientsApi.md#createCaregiverInvitation) | **POST** patients/{patientId}/caregiver-invitations | Invitar a un cuidador |
| [**createPatient**](PatientsApi.md#createPatient) | **POST** patients | Crear paciente gestionado |
| [**getPatient**](PatientsApi.md#getPatient) | **GET** patients/{patientId} | Detalle de un paciente |
| [**listPatientCaregivers**](PatientsApi.md#listPatientCaregivers) | **GET** patients/{patientId}/caregivers | Cuidadores de un paciente |
| [**listPatientConsents**](PatientsApi.md#listPatientConsents) | **GET** patients/{patientId}/consents | Consentimientos de un paciente |
| [**listPatients**](PatientsApi.md#listPatients) | **GET** patients | Pacientes accesibles |
| [**revokePatientCaregiver**](PatientsApi.md#revokePatientCaregiver) | **DELETE** patients/{patientId}/caregivers/{userId} | Revocar el vínculo de un cuidador |
| [**setPatientConsent**](PatientsApi.md#setPatientConsent) | **PUT** patients/{patientId}/consents/{purpose} | Otorgar o retirar un consentimiento del paciente |
| [**updatePatient**](PatientsApi.md#updatePatient) | **PATCH** patients/{patientId} | Actualizar un paciente |



Aceptar una invitación

Solo cuidadores (un paciente recibe 403). Crea o reactiva el vínculo y consume el código. Un código inexistente, caducado o ya usado responde 404 (&#x60;code: invitation_not_found&#x60;). Límite de intentos por usuario e IP (429) para impedir adivinar códigos. Si el cuidador ya tiene un vínculo activo con ese paciente: 409 (&#x60;code: already_linked&#x60;). 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(PatientsApi::class.java)
val code : kotlin.String = code_example // kotlin.String | 

launch(Dispatchers.IO) {
    val result : CareLink = webService.acceptCaregiverInvitation(code)
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **code** | **kotlin.String**|  | |

### Return type

[**CareLink**](CareLink.md)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json


Invitar a un cuidador

Genera un código de un solo uso, válido 72 h, que se comparte a mano con el cuidador (el envío por correo llega en la fase 4). Lo crea el paciente con cuenta o, en un paciente gestionado, un cuidador con vínculo activo. Límite de invitaciones por paciente y día (429). 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(PatientsApi::class.java)
val patientId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | 

launch(Dispatchers.IO) {
    val result : CaregiverInvitation = webService.createCaregiverInvitation(patientId)
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **patientId** | **java.util.UUID**|  | |

### Return type

[**CaregiverInvitation**](CaregiverInvitation.md)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json


Crear paciente gestionado

Solo cuidadores. Crea un paciente sin cuenta propia (por ejemplo, una persona mayor que solo usará el reloj) y un vínculo activo con el cuidador que lo crea. 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(PatientsApi::class.java)
val patientCreate : PatientCreate =  // PatientCreate | 

launch(Dispatchers.IO) {
    val result : Patient = webService.createPatient(patientCreate)
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **patientCreate** | [**PatientCreate**](PatientCreate.md)|  | |

### Return type

[**Patient**](Patient.md)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json, application/problem+json


Detalle de un paciente

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(PatientsApi::class.java)
val patientId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | 

launch(Dispatchers.IO) {
    val result : Patient = webService.getPatient(patientId)
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **patientId** | **java.util.UUID**|  | |

### Return type

[**Patient**](Patient.md)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json


Cuidadores de un paciente

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(PatientsApi::class.java)
val patientId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | 
val cursor : kotlin.String = cursor_example // kotlin.String | Cursor opaco devuelto como `nextCursor` en la página anterior.
val limit : kotlin.Int = 56 // kotlin.Int | 

launch(Dispatchers.IO) {
    val result : CareLinkPage = webService.listPatientCaregivers(patientId, cursor, limit)
}
```

### Parameters
| **patientId** | **java.util.UUID**|  | |
| **cursor** | **kotlin.String**| Cursor opaco devuelto como &#x60;nextCursor&#x60; en la página anterior. | [optional] |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **limit** | **kotlin.Int**|  | [optional] [default to 20] |

### Return type

[**CareLinkPage**](CareLinkPage.md)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json


Consentimientos de un paciente

Lo pueden leer el propio paciente y sus cuidadores con vínculo activo. Devuelve siempre las tres finalidades; las no respondidas llevan &#x60;granted &#x3D; false&#x60;. 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(PatientsApi::class.java)
val patientId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | 

launch(Dispatchers.IO) {
    val result : ConsentList = webService.listPatientConsents(patientId)
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **patientId** | **java.util.UUID**|  | |

### Return type

[**ConsentList**](ConsentList.md)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json


Pacientes accesibles

Un paciente ve solo su propio perfil. Un cuidador ve los pacientes con vínculo activo y los que él mismo gestiona. 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(PatientsApi::class.java)
val cursor : kotlin.String = cursor_example // kotlin.String | Cursor opaco devuelto como `nextCursor` en la página anterior.
val limit : kotlin.Int = 56 // kotlin.Int | 

launch(Dispatchers.IO) {
    val result : PatientPage = webService.listPatients(cursor, limit)
}
```

### Parameters
| **cursor** | **kotlin.String**| Cursor opaco devuelto como &#x60;nextCursor&#x60; en la página anterior. | [optional] |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **limit** | **kotlin.Int**|  | [optional] [default to 20] |

### Return type

[**PatientPage**](PatientPage.md)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json


Revocar el vínculo de un cuidador

Desactiva el vínculo (no se borra: queda para auditoría) y el cuidador deja de ver al paciente de inmediato. Pueden hacerlo: - el paciente con cuenta, sobre cualquiera de sus cuidadores; - el propio cuidador, sobre su vínculo; - en un paciente gestionado, cualquier cuidador con vínculo activo sobre otro, salvo el   último (409 &#x60;code: last_caregiver&#x60;, el paciente quedaría sin nadie que lo gestione). Idempotente: revocar un vínculo ya inactivo devuelve 204. 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(PatientsApi::class.java)
val patientId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | 
val userId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | Id de usuario del cuidador.

launch(Dispatchers.IO) {
    webService.revokePatientCaregiver(patientId, userId)
}
```

### Parameters
| **patientId** | **java.util.UUID**|  | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **userId** | **java.util.UUID**| Id de usuario del cuidador. | |

### Return type

null (empty response body)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/problem+json


Otorgar o retirar un consentimiento del paciente

- Paciente con cuenta: solo él (equivale a &#x60;PUT /users/me/consents/{purpose}&#x60;); un   cuidador recibe 403. - Paciente gestionado: cualquier cuidador con vínculo activo, en nombre del paciente. Idempotente. Cada cambio queda en el historial con fecha, versión del texto y &#x60;grantedBy&#x60; (usuario que decidió). 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(PatientsApi::class.java)
val patientId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | 
val purpose : ConsentPurpose =  // ConsentPurpose | 
val consentUpdate : ConsentUpdate =  // ConsentUpdate | 

launch(Dispatchers.IO) {
    val result : Consent = webService.setPatientConsent(patientId, purpose, consentUpdate)
}
```

### Parameters
| **patientId** | **java.util.UUID**|  | |
| **purpose** | [**ConsentPurpose**](.md)|  | [enum: health_data, ai_chat, notifications] |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **consentUpdate** | [**ConsentUpdate**](ConsentUpdate.md)|  | |

### Return type

[**Consent**](Consent.md)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json, application/problem+json


Actualizar un paciente

Un paciente con cuenta edita su propio perfil. Un paciente gestionado lo editan sus cuidadores con vínculo activo. Un cuidador sobre un paciente con cuenta recibe 403. 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(PatientsApi::class.java)
val patientId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | 
val patientUpdate : PatientUpdate =  // PatientUpdate | 

launch(Dispatchers.IO) {
    val result : Patient = webService.updatePatient(patientId, patientUpdate)
}
```

### Parameters
| **patientId** | **java.util.UUID**|  | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **patientUpdate** | [**PatientUpdate**](PatientUpdate.md)|  | |

### Return type

[**Patient**](Patient.md)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json, application/problem+json

