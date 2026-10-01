# UsersApi

All URIs are relative to *http://localhost:8000*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**getMe**](UsersApi.md#getMe) | **GET** users/me | Usuario autenticado |
| [**listMyConsents**](UsersApi.md#listMyConsents) | **GET** users/me/consents | Consentimientos del usuario |
| [**setMyConsent**](UsersApi.md#setMyConsent) | **PUT** users/me/consents/{purpose} | Otorgar o retirar un consentimiento propio |
| [**updateMe**](UsersApi.md#updateMe) | **PATCH** users/me | Actualizar perfil |



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

