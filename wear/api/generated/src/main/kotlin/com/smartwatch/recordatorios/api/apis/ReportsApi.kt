package com.smartwatch.recordatorios.api.apis

import com.smartwatch.recordatorios.api.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import okhttp3.ResponseBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import com.smartwatch.recordatorios.api.models.Problem
import com.smartwatch.recordatorios.api.models.Report
import com.smartwatch.recordatorios.api.models.ReportCreate
import com.smartwatch.recordatorios.api.models.ReportPage
import com.smartwatch.recordatorios.api.models.ValidationProblem

interface ReportsApi {
    /**
     * POST patients/{patientId}/reports
     * Pedir un reporte a mano
     * Genera el reporte de los 7 días que terminan en &#x60;periodEnd&#x60; (por defecto ayer, en la zona del paciente; no puede ser futuro). La generación es asíncrona: responde 202 con &#x60;status: pending&#x60; y la web consulta &#x60;GET /patients/{patientId}/reports/{reportId}&#x60; hasta &#x60;ready&#x60; o &#x60;failed&#x60;. Pedir el mismo &#x60;periodEnd&#x60; otra vez devuelve el reporte existente. Límite de peticiones por paciente (429). 
     * Responses:
     *  - 202: Reporte en generación (o el ya existente para ese periodo).
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 403: Autenticado pero sin permiso: rol que no permite la acción sobre un recurso accesible, o tipo de token equivocado (`wrong_token_type`). Nunca se usa para pacientes sin vínculo (eso es 404). 
     *  - 404: No existe o el usuario no tiene acceso: un cuidador que pide un paciente sin vínculo activo (o sus recursos) recibe 404, igual que si no existiera, para no revelar datos ajenos. 
     *  - 422: La petición no cumple el esquema.
     *  - 429: Límite de peticiones superado.
     *
     * @param patientId 
     * @param reportCreate  (optional)
     * @return [Report]
     */
    @POST("patients/{patientId}/reports")
    suspend fun createReport(@Path("patientId") patientId: java.util.UUID, @Body reportCreate: ReportCreate? = null): Response<Report>

    /**
     * GET patients/{patientId}/reports/{reportId}/pdf
     * Descargar el reporte en PDF
     * Con sesión de usuario (cabecera &#x60;Authorization&#x60;), no por enlace público: la web lo pide con &#x60;fetch&#x60; y lo descarga como blob. Reporte aún en generación o fallido: 409 (&#x60;code: report_not_ready&#x60;). 
     * Responses:
     *  - 200: Documento PDF.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 403: Autenticado pero sin permiso: rol que no permite la acción sobre un recurso accesible, o tipo de token equivocado (`wrong_token_type`). Nunca se usa para pacientes sin vínculo (eso es 404). 
     *  - 404: No existe o el usuario no tiene acceso: un cuidador que pide un paciente sin vínculo activo (o sus recursos) recibe 404, igual que si no existiera, para no revelar datos ajenos. 
     *  - 409: Conflicto con el estado actual (por ejemplo, correo ya registrado).
     *
     * @param patientId 
     * @param reportId 
     * @return [ResponseBody]
     */
    @GET("patients/{patientId}/reports/{reportId}/pdf")
    suspend fun downloadReportPdf(@Path("patientId") patientId: java.util.UUID, @Path("reportId") reportId: java.util.UUID): Response<ResponseBody>

    /**
     * GET patients/{patientId}/reports/{reportId}
     * Un reporte
     * Con &#x60;status: ready&#x60; incluye el resumen; el PDF está en &#x60;.../pdf&#x60;.
     * Responses:
     *  - 200: Reporte.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 403: Autenticado pero sin permiso: rol que no permite la acción sobre un recurso accesible, o tipo de token equivocado (`wrong_token_type`). Nunca se usa para pacientes sin vínculo (eso es 404). 
     *  - 404: No existe o el usuario no tiene acceso: un cuidador que pide un paciente sin vínculo activo (o sus recursos) recibe 404, igual que si no existiera, para no revelar datos ajenos. 
     *
     * @param patientId 
     * @param reportId 
     * @return [Report]
     */
    @GET("patients/{patientId}/reports/{reportId}")
    suspend fun getReport(@Path("patientId") patientId: java.util.UUID, @Path("reportId") reportId: java.util.UUID): Response<Report>

    /**
     * GET patients/{patientId}/reports
     * Reportes semanales de un paciente
     * Del más reciente al más antiguo. Hay un reporte automático por semana (lunes a domingo) que se genera el lunes a las 08:00 en la zona horaria del paciente; también pueden pedirse a mano. Requiere vínculo con el paciente (404 si no) y consentimiento &#x60;health_data&#x60; (403 &#x60;consent_required&#x60;). 
     * Responses:
     *  - 200: Página de reportes.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 403: Autenticado pero sin permiso: rol que no permite la acción sobre un recurso accesible, o tipo de token equivocado (`wrong_token_type`). Nunca se usa para pacientes sin vínculo (eso es 404). 
     *  - 404: No existe o el usuario no tiene acceso: un cuidador que pide un paciente sin vínculo activo (o sus recursos) recibe 404, igual que si no existiera, para no revelar datos ajenos. 
     *  - 422: La petición no cumple el esquema.
     *
     * @param patientId 
     * @param cursor Cursor opaco devuelto como &#x60;nextCursor&#x60; en la página anterior. (optional)
     * @param limit  (optional, default to 20)
     * @return [ReportPage]
     */
    @GET("patients/{patientId}/reports")
    suspend fun listReports(@Path("patientId") patientId: java.util.UUID, @Query("cursor") cursor: kotlin.String? = null, @Query("limit") limit: kotlin.Int? = 20): Response<ReportPage>

}
