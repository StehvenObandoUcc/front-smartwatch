# UsersApi

All URIs are relative to *http://localhost:8000*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**deleteMe**](UsersApi.md#deleteMe) | **DELETE** users/me | Eliminar la cuenta |
| [**getMe**](UsersApi.md#getMe) | **GET** users/me | Usuario autenticado |
| [**listMyConsents**](UsersApi.md#listMyConsents) | **GET** users/me/consents | Consentimientos del usuario |
| [**setMyConsent**](UsersApi.md#setMyConsent) | **PUT** users/me/consents/{purpose} | Otorgar o retirar un consentimiento propio |
| [**updateMe**](UsersApi.md#updateMe) | **PATCH** users/me | Actualizar perfil |



Eliminar la cuenta

Derecho de supresión (Ley 1581 de 2012). Exige la contraseña actual en el cuerpo y es **irreversible**: todo ocurre en una sola transacción. - Se borra la cuenta, sus sesiones, su vinculación de Telegram y sus preferencias. - Si es un paciente con cuenta, se borra su registro de paciente con todo su contenido   (medicamentos, horarios, tomas, reportes, relojes, vínculos y consentimientos). - Si es cuidador, cada paciente gestionado donde es el **único** cuidador activo se borra   con todo su contenido; si hay otros cuidadores activos, el paciente se conserva y solo   desaparece el vínculo de este usuario. - Se borra el uso del chat y los mensajes de notificación aún sin enviar dirigidos a su   correo o a su chat de Telegram. - Los consentimientos que otros conservan (p. ej. los que dio en nombre de un paciente   gestionado que sigue existiendo) se mantienen, con &#x60;grantedBy: null&#x60;. - Los tokens de acceso ya emitidos caducan solos (15 min) y dejan de servir: el usuario   ya no existe (401 &#x60;invalid_token&#x60;). Los refresh tokens y los relojes quedan invalidados. Responde 403 &#x60;invalid_password&#x60; (no 401, para que el cliente web no intente renovar la sesión) si la contraseña no es correcta. Límite de intentos por usuario: 429 con &#x60;Retry-After&#x60;. 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(UsersApi::class.java)
val deleteAccountRequest : DeleteAccountRequest =  // DeleteAccountRequest | 

launch(Dispatchers.IO) {
    webService.deleteMe(deleteAccountRequest)
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **deleteAccountRequest** | [**DeleteAccountRequest**](DeleteAccountRequest.md)|  | |

### Return type

null (empty response body)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/problem+json


Usuario autenticado

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(UsersApi::class.java)

launch(Dispatchers.IO) {
    val result : User = webService.getMe()
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**User**](User.md)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json


Consentimientos del usuario

Consentimientos del usuario autenticado sobre sus propios datos. Para un usuario con rol &#x60;patient&#x60; son los mismos registros que &#x60;GET /patients/{patientId}/consents&#x60; de su perfil. Devuelve siempre las tres finalidades; las no respondidas llevan &#x60;granted &#x3D; false&#x60;. 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(UsersApi::class.java)

launch(Dispatchers.IO) {
    val result : ConsentList = webService.listMyConsents()
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**ConsentList**](ConsentList.md)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json


Otorgar o retirar un consentimiento propio

Idempotente. Cada cambio queda registrado (historial) con fecha, versión del texto aceptado y &#x60;grantedBy&#x60; &#x3D; el propio usuario. 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(UsersApi::class.java)
val purpose : ConsentPurpose =  // ConsentPurpose | 
val consentUpdate : ConsentUpdate =  // ConsentUpdate | 

launch(Dispatchers.IO) {
    val result : Consent = webService.setMyConsent(purpose, consentUpdate)
}
```

### Parameters
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


Actualizar perfil

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(UsersApi::class.java)
val userUpdate : UserUpdate =  // UserUpdate | 

launch(Dispatchers.IO) {
    val result : User = webService.updateMe(userUpdate)
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **userUpdate** | [**UserUpdate**](UserUpdate.md)|  | |

### Return type

[**User**](User.md)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json, application/problem+json

