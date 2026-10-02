# NotificationsApi

All URIs are relative to *http://localhost:8000*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**createTelegramLink**](NotificationsApi.md#createTelegramLink) | **POST** users/me/notification-channels/telegram/link | Pedir el enlace para vincular Telegram |
| [**getMyNotificationPreferences**](NotificationsApi.md#getMyNotificationPreferences) | **GET** users/me/notification-preferences | Preferencias de notificación |
| [**listMyNotificationChannels**](NotificationsApi.md#listMyNotificationChannels) | **GET** users/me/notification-channels | Canales de notificación del usuario |
| [**setMyNotificationPreferences**](NotificationsApi.md#setMyNotificationPreferences) | **PUT** users/me/notification-preferences | Guardar las preferencias de notificación |
| [**unlinkTelegram**](NotificationsApi.md#unlinkTelegram) | **DELETE** users/me/notification-channels/telegram | Desvincular Telegram |



Pedir el enlace para vincular Telegram

Devuelve &#x60;https://t.me/&lt;Bot&gt;?start&#x3D;&lt;token&gt;&#x60;. El token es de un solo uso y caduca a los 15 minutos; pedir otro invalida el anterior. Al abrir el enlace y pulsar \&quot;Iniciar\&quot;, el bot recibe el token por el webhook y vincula el chat con el usuario; el bot solo puede escribir a quien lo inició. La web consulta &#x60;GET /users/me/notification-channels&#x60; hasta ver &#x60;telegram.linked: true&#x60;. Límite de peticiones por usuario (429). 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(NotificationsApi::class.java)

launch(Dispatchers.IO) {
    val result : TelegramLink = webService.createTelegramLink()
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**TelegramLink**](TelegramLink.md)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json


Preferencias de notificación

Qué avisos llegan por qué canal. Por defecto todo activado; un aviso solo se envía si el canal está vinculado (y el correo verificado) y el usuario concedió el consentimiento &#x60;notifications&#x60; (el del paciente, si el usuario es un paciente con cuenta). 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(NotificationsApi::class.java)

launch(Dispatchers.IO) {
    val result : NotificationPreferences = webService.getMyNotificationPreferences()
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**NotificationPreferences**](NotificationPreferences.md)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json


Canales de notificación del usuario

Siempre devuelve los dos canales. &#x60;email&#x60; es el correo de la cuenta (&#x60;linked&#x60; siempre true; &#x60;verified&#x60; indica si se verificó). &#x60;telegram&#x60; está vinculado cuando el usuario abrió el enlace de &#x60;POST /users/me/notification-channels/telegram/link&#x60;. 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(NotificationsApi::class.java)

launch(Dispatchers.IO) {
    val result : NotificationChannelList = webService.listMyNotificationChannels()
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**NotificationChannelList**](NotificationChannelList.md)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json


Guardar las preferencias de notificación

Reemplazo completo e idempotente.

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(NotificationsApi::class.java)
val notificationPreferences : NotificationPreferences =  // NotificationPreferences | 

launch(Dispatchers.IO) {
    val result : NotificationPreferences = webService.setMyNotificationPreferences(notificationPreferences)
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **notificationPreferences** | [**NotificationPreferences**](NotificationPreferences.md)|  | |

### Return type

[**NotificationPreferences**](NotificationPreferences.md)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json, application/problem+json


Desvincular Telegram

Idempotente: si no estaba vinculado devuelve 204.

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(NotificationsApi::class.java)

launch(Dispatchers.IO) {
    webService.unlinkTelegram()
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

null (empty response body)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/problem+json

