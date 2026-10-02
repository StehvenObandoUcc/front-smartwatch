# ReportsApi

All URIs are relative to *http://localhost:8000*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**createReport**](ReportsApi.md#createReport) | **POST** patients/{patientId}/reports | Pedir un reporte a mano |
| [**downloadReportPdf**](ReportsApi.md#downloadReportPdf) | **GET** patients/{patientId}/reports/{reportId}/pdf | Descargar el reporte en PDF |
| [**getReport**](ReportsApi.md#getReport) | **GET** patients/{patientId}/reports/{reportId} | Un reporte |
| [**listReports**](ReportsApi.md#listReports) | **GET** patients/{patientId}/reports | Reportes semanales de un paciente |



Pedir un reporte a mano

Genera el reporte de los 7 días que terminan en &#x60;periodEnd&#x60; (por defecto ayer, en la zona del paciente; no puede ser futuro). La generación es asíncrona: responde 202 con &#x60;status: pending&#x60; y la web consulta &#x60;GET /patients/{patientId}/reports/{reportId}&#x60; hasta &#x60;ready&#x60; o &#x60;failed&#x60;. Pedir el mismo &#x60;periodEnd&#x60; otra vez devuelve el reporte existente. Límite de peticiones por paciente (429). 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(ReportsApi::class.java)
val patientId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | 
val reportCreate : ReportCreate =  // ReportCreate | 

launch(Dispatchers.IO) {
    val result : Report = webService.createReport(patientId, reportCreate)
}
```

### Parameters
| **patientId** | **java.util.UUID**|  | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **reportCreate** | [**ReportCreate**](ReportCreate.md)|  | [optional] |

### Return type

[**Report**](Report.md)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json, application/problem+json


Descargar el reporte en PDF

Con sesión de usuario (cabecera &#x60;Authorization&#x60;), no por enlace público: la web lo pide con &#x60;fetch&#x60; y lo descarga como blob. Reporte aún en generación o fallido: 409 (&#x60;code: report_not_ready&#x60;). 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(ReportsApi::class.java)
val patientId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | 
val reportId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | 

launch(Dispatchers.IO) {
    val result : java.io.File = webService.downloadReportPdf(patientId, reportId)
}
```

### Parameters
| **patientId** | **java.util.UUID**|  | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **reportId** | **java.util.UUID**|  | |

### Return type

[**java.io.File**](java.io.File.md)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/pdf, application/problem+json


Un reporte

Con &#x60;status: ready&#x60; incluye el resumen; el PDF está en &#x60;.../pdf&#x60;.

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(ReportsApi::class.java)
val patientId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | 
val reportId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | 

launch(Dispatchers.IO) {
    val result : Report = webService.getReport(patientId, reportId)
}
```

### Parameters
| **patientId** | **java.util.UUID**|  | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **reportId** | **java.util.UUID**|  | |

### Return type

[**Report**](Report.md)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json


Reportes semanales de un paciente

Del más reciente al más antiguo. Hay un reporte automático por semana (lunes a domingo) que se genera el lunes a las 08:00 en la zona horaria del paciente; también pueden pedirse a mano. Requiere vínculo con el paciente (404 si no) y consentimiento &#x60;health_data&#x60; (403 &#x60;consent_required&#x60;). 

### Example
```kotlin
// Import classes:
//import com.smartwatch.recordatorios.api.*
//import com.smartwatch.recordatorios.api.infrastructure.*
//import com.smartwatch.recordatorios.api.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(ReportsApi::class.java)
val patientId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | 
val cursor : kotlin.String = cursor_example // kotlin.String | Cursor opaco devuelto como `nextCursor` en la página anterior.
val limit : kotlin.Int = 56 // kotlin.Int | 

launch(Dispatchers.IO) {
    val result : ReportPage = webService.listReports(patientId, cursor, limit)
}
```

### Parameters
| **patientId** | **java.util.UUID**|  | |
| **cursor** | **kotlin.String**| Cursor opaco devuelto como &#x60;nextCursor&#x60; en la página anterior. | [optional] |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **limit** | **kotlin.Int**|  | [optional] [default to 20] |

### Return type

[**ReportPage**](ReportPage.md)

### Authorization


Configure bearerAuth:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json

