
# PlannedDose

## Properties
| Name | Type | Description | Notes |
| ------------ | ------------- | ------------- | ------------- |
| **scheduleId** | [**java.util.UUID**](java.util.UUID.md) |  |  |
| **medicationId** | [**java.util.UUID**](java.util.UUID.md) |  |  |
| **medicationName** | **kotlin.String** |  |  |
| **dosage** | **kotlin.String** |  |  |
| **color** | **kotlin.String** |  |  |
| **scheduledAt** | [**java.time.OffsetDateTime**](java.time.OffsetDateTime.md) | Instante de la dosis con el desfase de la zona del paciente. La dosis se identifica por &#x60;scheduleId&#x60; + &#x60;scheduledAt&#x60;. |  |
| **instructions** | **kotlin.String** |  |  [optional] |



