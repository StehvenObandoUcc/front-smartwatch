
# PairingCode

## Properties
| Name | Type | Description | Notes |
| ------------ | ------------- | ------------- | ------------- |
| **code** | **kotlin.String** | Código corto que muestra el reloj (formato XXXX-XXXX, alfabeto RFC 8628). |  |
| **deviceCode** | **kotlin.String** | Secreto que solo conoce el reloj. |  |
| **expiresIn** | **kotlin.Int** | Segundos de validez del código. |  |
| **interval** | **kotlin.Int** | Segundos mínimos entre consultas a &#x60;POST /devices/token&#x60;. |  |



