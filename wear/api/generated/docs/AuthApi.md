# AuthApi

All URIs are relative to *http://localhost:8000*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**forgotPassword**](AuthApi.md#forgotPassword) | **POST** auth/forgot-password | Pedir el correo de recuperación de contraseña |
| [**login**](AuthApi.md#login) | **POST** auth/login | Iniciar sesión |
| [**logout**](AuthApi.md#logout) | **POST** auth/logout | Cerrar sesión web |
| [**refreshTokens**](AuthApi.md#refreshTokens) | **POST** auth/refresh | Renovar la sesión web |
| [**register**](AuthApi.md#register) | **POST** auth/register | Crear cuenta |
| [**resendVerification**](AuthApi.md#resendVerification) | **POST** auth/resend-verification | Reenviar el correo de verificación |
| [**resetPassword**](AuthApi.md#resetPassword) | **POST** auth/reset-password | Fijar una contraseña nueva con el token de recuperación |
| [**verifyEmail**](AuthApi.md#verifyEmail) | **POST** auth/verify-email | Verificar el correo con el token recibido |



Pedir el correo de recuperación de contraseña

Responde siempre 202, exista o no el correo, para no revelar qué cuentas existen. Si existe y está verificado, se envía un enlace al panel (&#x60;{WEB_ORIGIN}/reset-password?token&#x3D;...&#x60;) con un token de un solo uso que caduca a la hora. Límite de peticiones por IP y por correo (429). 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(AuthApi::class.java)
val forgotPasswordRequest : ForgotPasswordRequest =  // ForgotPasswordRequest | 

launch(Dispatchers.IO) {
    webService.forgotPassword(forgotPasswordRequest)
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **forgotPasswordRequest** | [**ForgotPasswordRequest**](ForgotPasswordRequest.md)|  | |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/problem+json


Iniciar sesión

Abre sesión web: token de acceso en el JSON y refresh token en la cookie &#x60;__Secure-refresh-token&#x60;. Credenciales incorrectas: 401 (&#x60;code: invalid_credentials&#x60;), sin distinguir si el correo existe. Límite de peticiones por IP y por correo (429). 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(AuthApi::class.java)
val loginRequest : LoginRequest =  // LoginRequest | 

launch(Dispatchers.IO) {
    val result : AuthSession = webService.login(loginRequest)
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **loginRequest** | [**LoginRequest**](LoginRequest.md)|  | |

### Return type

[**AuthSession**](AuthSession.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json, application/problem+json


Cerrar sesión web

Sin cuerpo y sin token de acceso (puede haber caducado). Revoca la familia del refresh token de la cookie, si la hay, y borra la cookie. Idempotente: sin cookie también responde 204. 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(AuthApi::class.java)
val secureRefreshToken : kotlin.String = secureRefreshToken_example // kotlin.String | Refresh token de la sesión web. Lo envía el navegador; JavaScript no puede leerlo.

launch(Dispatchers.IO) {
    webService.logout(secureRefreshToken)
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **secureRefreshToken** | **kotlin.String**| Refresh token de la sesión web. Lo envía el navegador; JavaScript no puede leerlo. | [optional] |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined


Renovar la sesión web

Sin cuerpo. Lee el refresh token de la cookie &#x60;__Secure-refresh-token&#x60;, lo rota y devuelve un token de acceso nuevo en el JSON y el refresh token nuevo en la cookie. - Sin cookie, o refresh token inválido o caducado: 401 y la cookie se borra. - Reutilizar un refresh token ya rotado revoca toda la familia (401,   &#x60;code: refresh_token_reused&#x60;) y la cookie se borra. - Un refresh token de reloj aquí da 401; los relojes renuevan con &#x60;POST /devices/token&#x60;. 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(AuthApi::class.java)
val secureRefreshToken : kotlin.String = secureRefreshToken_example // kotlin.String | Refresh token de la sesión web. Lo envía el navegador; JavaScript no puede leerlo.

launch(Dispatchers.IO) {
    val result : AccessToken = webService.refreshTokens(secureRefreshToken)
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **secureRefreshToken** | **kotlin.String**| Refresh token de la sesión web. Lo envía el navegador; JavaScript no puede leerlo. | [optional] |

### Return type

[**AccessToken**](AccessToken.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json


Crear cuenta

Crea un usuario y abre sesión web. Si &#x60;role&#x60; es &#x60;patient&#x60;, en la misma transacción se crea su registro en &#x60;/patients&#x60; (&#x60;managed: false&#x60;, con el &#x60;displayName&#x60; y &#x60;timezone&#x60; del registro) y &#x60;user.patientId&#x60; apunta a él. Si &#x60;role&#x60; es &#x60;caregiver&#x60;, &#x60;user.patientId&#x60; es null. Devuelve el token de acceso en el JSON y el refresh token en la cookie &#x60;__Secure-refresh-token&#x60;. Límite de peticiones por IP (429). Encola un correo de verificación (&#x60;POST /auth/verify-email&#x60;); no hace falta verificarlo para usar la cuenta. 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(AuthApi::class.java)
val registerRequest : RegisterRequest =  // RegisterRequest | 

launch(Dispatchers.IO) {
    val result : AuthSession = webService.register(registerRequest)
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **registerRequest** | [**RegisterRequest**](RegisterRequest.md)|  | |

### Return type

[**AuthSession**](AuthSession.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json, application/problem+json


Reenviar el correo de verificación

Solo con sesión de usuario. Invalida el token anterior. Si el correo ya está verificado devuelve 204 sin enviar nada. Límite de peticiones por usuario (429). 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(AuthApi::class.java)

launch(Dispatchers.IO) {
    webService.resendVerification()
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


Fijar una contraseña nueva con el token de recuperación

Token inválido, caducado o ya usado: 400 (&#x60;code: invalid_token&#x60;). Al cambiar la contraseña se revocan todos los refresh tokens del usuario (web); los relojes vinculados no se tocan. No abre sesión: la web lleva al usuario a iniciarla. 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(AuthApi::class.java)
val resetPasswordRequest : ResetPasswordRequest =  // ResetPasswordRequest | 

launch(Dispatchers.IO) {
    webService.resetPassword(resetPasswordRequest)
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **resetPasswordRequest** | [**ResetPasswordRequest**](ResetPasswordRequest.md)|  | |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/problem+json


Verificar el correo con el token recibido

El token llega por correo en un enlace al panel (&#x60;{WEB_ORIGIN}/verify-email?token&#x3D;...&#x60;), es de un solo uso y caduca a las 24 horas. Token inválido, caducado o ya usado: 400 (&#x60;code: invalid_token&#x60;). Verificar un correo ya verificado con un token válido devuelve 204. 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(AuthApi::class.java)
val tokenBody : TokenBody =  // TokenBody | 

launch(Dispatchers.IO) {
    webService.verifyEmail(tokenBody)
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **tokenBody** | [**TokenBody**](TokenBody.md)|  | |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/problem+json

