
# ChatAction

## Properties
| Name | Type | Description | Notes |
| ------------ | ------------- | ------------- | ------------- |
| **type** | [**inline**](#Type) |  |  |
| **medicationId** | [**java.util.UUID**](java.util.UUID.md) |  |  |
| **scheduleId** | [**java.util.UUID**](java.util.UUID.md) | Horario creado o cambiado; &#x60;null&#x60; si el cambio no toca horarios. |  |


<a id="Type"></a>
## Enum: type
| Name | Value |
| ---- | ----- |
| type | medication_created, schedule_added, schedule_updated, dosage_updated, medication_archived |



