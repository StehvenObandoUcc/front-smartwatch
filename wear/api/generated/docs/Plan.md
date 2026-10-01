
# Plan

## Properties
| Name | Type | Description | Notes |
| ------------ | ------------- | ------------- | ------------- |
| **version** | **kotlin.Int** | Sube con cada cambio de medicamentos u horarios del paciente. |  |
| **patientTimezone** | **kotlin.String** | Zona horaria IANA. |  |
| **generatedAt** | [**java.time.OffsetDateTime**](java.time.OffsetDateTime.md) |  |  |
| **validFrom** | [**java.time.OffsetDateTime**](java.time.OffsetDateTime.md) | Inicio del día local del paciente en que se generó. |  |
| **validUntil** | [**java.time.OffsetDateTime**](java.time.OffsetDateTime.md) | Fin del séptimo día (exclusivo). |  |
| **doses** | [**kotlin.collections.List&lt;PlannedDose&gt;**](PlannedDose.md) | Ordenadas por &#x60;scheduledAt&#x60;. |  |



