# HealthApi

All URIs are relative to *http://localhost:8000*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**getHealth**](HealthApi.md#getHealth) | **GET** health | Liveness |
| [**getReadiness**](HealthApi.md#getReadiness) | **GET** health/ready | Readiness |



Liveness

Indica que el proceso está vivo. No consulta dependencias.

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(HealthApi::class.java)

launch(Dispatchers.IO) {
    val result : Health = webService.getHealth()
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**Health**](Health.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


Readiness

Comprueba las dependencias (Postgres y Redis).

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(HealthApi::class.java)

launch(Dispatchers.IO) {
    val result : Readiness = webService.getReadiness()
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**Readiness**](Readiness.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json

