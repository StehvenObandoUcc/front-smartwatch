
# MedicationAdherence

## Properties
| Name | Type | Description | Notes |
| ------------ | ------------- | ------------- | ------------- |
| **medicationId** | [**java.util.UUID**](java.util.UUID.md) |  |  |
| **medicationName** | **kotlin.String** |  |  |
| **dosage** | **kotlin.String** | Dosis vigente del medicamento al final del periodo (por ejemplo \&quot;850 mg\&quot;). |  |
| **scheduled** | **kotlin.Int** | Dosis programadas del periodo (&#x60;taken + skipped + missed&#x60;). |  |
| **taken** | **kotlin.Int** |  |  |
| **skipped** | **kotlin.Int** |  |  |
| **missed** | **kotlin.Int** |  |  |
| **percentage** | [**java.math.BigDecimal**](java.math.BigDecimal.md) |  |  |



