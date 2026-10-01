# DevicesApi

All URIs are relative to *http://localhost:8000*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**confirmPairingCode**](DevicesApi.md#confirmPairingCode) | **POST** devices/pairing-codes/{code}/confirm | Confirmar la vinculación desde la web |
| [**createPairingCode**](DevicesApi.md#createPairingCode) | **POST** devices/pairing-codes | El reloj pide un código de vinculación |
| [**getMyDevice**](DevicesApi.md#getMyDevice) | **GET** devices/me | El reloj consulta su vinculación |
| [**listPatientDevices**](DevicesApi.md#listPatientDevices) | **GET** patients/{patientId}/devices | Relojes de un paciente |
| [**requestDeviceToken**](DevicesApi.md#requestDeviceToken) | **POST** devices/token | El reloj obtiene o renueva sus tokens |
| [**setMyPushToken**](DevicesApi.md#setMyPushToken) | **PUT** devices/me/push-token | Registrar el token FCM del reloj |
| [**unpairDevice**](DevicesApi.md#unpairDevice) | **DELETE** devices/{deviceId} | Desvincular un reloj |



Confirmar la vinculación desde la web

Paso 2. Desde la web, el usuario escribe el código que muestra el reloj y envía en el cuerpo el &#x60;patientId&#x60; al que se vinculará. Puede hacerlo el paciente con cuenta (sobre sí mismo) o un cuidador con vínculo activo. - Código inexistente, caducado o ya confirmado: 404 (&#x60;code: pairing_code_not_found&#x60;). - &#x60;patientId&#x60; sin vínculo activo con el usuario: 404 (&#x60;code: patient_not_found&#x60;). - Límite de intentos por usuario e IP (429) para impedir adivinar códigos. Un paciente solo tiene un reloj activo: si ya había otro, se desvincula y se revocan sus tokens. 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(DevicesApi::class.java)
val code : kotlin.String = code_example // kotlin.String | 
val pairingConfirm : PairingConfirm =  // PairingConfirm | 

launch(Dispatchers.IO) {
    val result : Device = webService.confirmPairingCode(code, pairingConfirm)
}
```

### Parameters
| **code** | **kotlin.String**|  | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **pairingConfirm** | [**PairingConfirm**](PairingConfirm.md)|  | |

### Return type

[**Device**](Device.md)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json, application/problem+json


El reloj pide un código de vinculación

Paso 1 del flujo tipo device code (RFC 8628). El reloj muestra &#x60;code&#x60; en pantalla y guarda &#x60;deviceCode&#x60; en secreto para consultar &#x60;POST /devices/token&#x60; cada &#x60;interval&#x60; segundos. Ambos son de un solo uso y caducan a los &#x60;expiresIn&#x60; segundos (10 min). Límite de peticiones por IP (429). 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(DevicesApi::class.java)
val pairingCodeRequest : PairingCodeRequest =  // PairingCodeRequest | 

launch(Dispatchers.IO) {
    val result : PairingCode = webService.createPairingCode(pairingCodeRequest)
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **pairingCodeRequest** | [**PairingCodeRequest**](PairingCodeRequest.md)|  | |

### Return type

[**PairingCode**](PairingCode.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json, application/problem+json


El reloj consulta su vinculación

Solo con token de reloj; un token de usuario recibe 403.

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(DevicesApi::class.java)

launch(Dispatchers.IO) {
    val result : Device = webService.getMyDevice()
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**Device**](Device.md)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json


Relojes de un paciente

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(DevicesApi::class.java)
val patientId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | 
val cursor : kotlin.String = cursor_example // kotlin.String | Cursor opaco devuelto como `nextCursor` en la página anterior.
val limit : kotlin.Int = 56 // kotlin.Int | 

launch(Dispatchers.IO) {
    val result : DevicePage = webService.listPatientDevices(patientId, cursor, limit)
}
```

### Parameters
| **patientId** | **java.util.UUID**|  | |
| **cursor** | **kotlin.String**| Cursor opaco devuelto como &#x60;nextCursor&#x60; en la página anterior. | [optional] |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **limit** | **kotlin.Int**|  | [optional] [default to 20] |

### Return type

[**DevicePage**](DevicePage.md)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json


El reloj obtiene o renueva sus tokens

Único endpoint de tokens del reloj, según &#x60;grantType&#x60;:  **&#x60;device_code&#x60;** (paso 3 de la vinculación). Mientras la web no confirme responde 400 con &#x60;code&#x60;: - &#x60;authorization_pending&#x60;: sigue esperando. - &#x60;slow_down&#x60;: consulta demasiado rápido; suma 5 s a &#x60;interval&#x60;. - &#x60;expired_token&#x60;: el código caducó o ya se usó; hay que pedir otro. Tras la confirmación devuelve los tokens una sola vez y el &#x60;deviceCode&#x60; queda consumido.  **&#x60;refresh_token&#x60;** (renovación). El reloj renueva su token de acceso antes de que caduque o al recibir 401. El refresh token rota en cada uso; reutilizar uno ya rotado revoca los tokens del reloj (&#x60;code: refresh_token_reused&#x60;) y hay que volver a vincularlo. Un refresh token inválido, caducado (90 días sin uso) o de un reloj desvinculado responde 401. Un refresh token de usuario aquí también responde 401.  Límite de peticiones por IP y por &#x60;deviceCode&#x60; (429). 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(DevicesApi::class.java)
val kotlinxSerializationJsonJsonObject : kotlinx.serialization.json.JsonObject =  // kotlinx.serialization.json.JsonObject | 

launch(Dispatchers.IO) {
    val result : TokenPair = webService.requestDeviceToken(kotlinxSerializationJsonJsonObject)
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **kotlinxSerializationJsonJsonObject** | [**kotlinx.serialization.json.JsonObject**](kotlinx.serialization.json.JsonObject.md)|  | |

### Return type

[**TokenPair**](TokenPair.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json, application/problem+json


Registrar el token FCM del reloj

Solo con token de reloj; un token de usuario recibe 403. Idempotente; reemplaza el anterior.

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(DevicesApi::class.java)
val pushTokenUpdate : PushTokenUpdate =  // PushTokenUpdate | 

launch(Dispatchers.IO) {
    webService.setMyPushToken(pushTokenUpdate)
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **pushTokenUpdate** | [**PushTokenUpdate**](PushTokenUpdate.md)|  | |

### Return type

null (empty response body)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/problem+json


Desvincular un reloj

Lo hacen el paciente con cuenta o un cuidador con vínculo activo. Revoca los tokens del reloj de inmediato. Un reloj de un paciente sin vínculo con el usuario responde 404. Idempotente: desvincular un reloj ya desvinculado devuelve 204. 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(DevicesApi::class.java)
val deviceId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | 

launch(Dispatchers.IO) {
    webService.unpairDevice(deviceId)
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **deviceId** | **java.util.UUID**|  | |

### Return type

null (empty response body)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/problem+json

