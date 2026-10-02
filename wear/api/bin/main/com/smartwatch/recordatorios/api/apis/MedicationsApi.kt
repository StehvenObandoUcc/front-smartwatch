package com.smartwatch.recordatorios.api.apis

import com.smartwatch.recordatorios.api.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import com.smartwatch.recordatorios.api.models.Medication
import com.smartwatch.recordatorios.api.models.MedicationCreate
import com.smartwatch.recordatorios.api.models.MedicationPage
import com.smartwatch.recordatorios.api.models.MedicationUpdate
import com.smartwatch.recordatorios.api.models.Problem
import com.smartwatch.recordatorios.api.models.Schedule
import com.smartwatch.recordatorios.api.models.ScheduleCreate
import com.smartwatch.recordatorios.api.models.ScheduleList
import com.smartwatch.recordatorios.api.models.ScheduleUpdate
import com.smartwatch.recordatorios.api.models.ValidationProblem

interface MedicationsApi {
    /**
     * DELETE patients/{patientId}/medications/{medicationId}
     * Archivar un medicamento
     * Borrado lógico: deja de aparecer en el plan pero se conserva su historial de tomas. Idempotente. Sube la versión del plan. 
     * Responses:
     *  - 204: Medicamento archivado.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 403: Autenticado pero sin permiso: rol que no permite la acción sobre un recurso accesible, o tipo de token equivocado (`wrong_token_type`). Nunca se usa para pacientes sin vínculo (eso es 404). 
     *  - 404: No existe o el usuario no tiene acceso: un cuidador que pide un paciente sin vínculo activo (o sus recursos) recibe 404, igual que si no existiera, para no revelar datos ajenos. 
     *
     * @param patientId 
     * @param medicationId 
     * @return [Unit]
     */
    @DELETE("patients/{patientId}/medications/{medicationId}")
    suspend fun archiveMedication(@Path("patientId") patientId: java.util.UUID, @Path("medicationId") medicationId: java.util.UUID): Response<Unit>

    /**
     * POST patients/{patientId}/medications
     * Crear un medicamento
     * Sube la versión del plan del paciente. Mismas reglas de acceso y consentimiento que el listado.
     * Responses:
     *  - 201: Medicamento creado.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 403: Autenticado pero sin permiso: rol que no permite la acción sobre un recurso accesible, o tipo de token equivocado (`wrong_token_type`). Nunca se usa para pacientes sin vínculo (eso es 404). 
     *  - 404: No existe o el usuario no tiene acceso: un cuidador que pide un paciente sin vínculo activo (o sus recursos) recibe 404, igual que si no existiera, para no revelar datos ajenos. 
     *  - 422: La petición no cumple el esquema.
     *
     * @param patientId 
     * @param medicationCreate 
     * @return [Medication]
     */
    @POST("patients/{patientId}/medications")
    suspend fun createMedication(@Path("patientId") patientId: java.util.UUID, @Body medicationCreate: MedicationCreate): Response<Medication>

    /**
     * POST patients/{patientId}/medications/{medicationId}/schedules
     * Añadir un horario
     * Sube la versión del plan. Máximo 10 horarios por medicamento (422 si se supera).
     * Responses:
     *  - 201: Horario creado.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 403: Autenticado pero sin permiso: rol que no permite la acción sobre un recurso accesible, o tipo de token equivocado (`wrong_token_type`). Nunca se usa para pacientes sin vínculo (eso es 404). 
     *  - 404: No existe o el usuario no tiene acceso: un cuidador que pide un paciente sin vínculo activo (o sus recursos) recibe 404, igual que si no existiera, para no revelar datos ajenos. 
     *  - 422: La petición no cumple el esquema.
     *
     * @param patientId 
     * @param medicationId 
     * @param scheduleCreate 
     * @return [Schedule]
     */
    @POST("patients/{patientId}/medications/{medicationId}/schedules")
    suspend fun createSchedule(@Path("patientId") patientId: java.util.UUID, @Path("medicationId") medicationId: java.util.UUID, @Body scheduleCreate: ScheduleCreate): Response<Schedule>

    /**
     * DELETE patients/{patientId}/medications/{medicationId}/schedules/{scheduleId}
     * Eliminar un horario
     * Idempotente. Sube la versión del plan. Las tomas ya registradas se conservan.
     * Responses:
     *  - 204: Horario eliminado.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 403: Autenticado pero sin permiso: rol que no permite la acción sobre un recurso accesible, o tipo de token equivocado (`wrong_token_type`). Nunca se usa para pacientes sin vínculo (eso es 404). 
     *  - 404: No existe o el usuario no tiene acceso: un cuidador que pide un paciente sin vínculo activo (o sus recursos) recibe 404, igual que si no existiera, para no revelar datos ajenos. 
     *
     * @param patientId 
     * @param medicationId 
     * @param scheduleId 
     * @return [Unit]
     */
    @DELETE("patients/{patientId}/medications/{medicationId}/schedules/{scheduleId}")
    suspend fun deleteSchedule(@Path("patientId") patientId: java.util.UUID, @Path("medicationId") medicationId: java.util.UUID, @Path("scheduleId") scheduleId: java.util.UUID): Response<Unit>

    /**
     * GET patients/{patientId}/medications/{medicationId}
     * Un medicamento
     * 
     * Responses:
     *  - 200: Medicamento.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 403: Autenticado pero sin permiso: rol que no permite la acción sobre un recurso accesible, o tipo de token equivocado (`wrong_token_type`). Nunca se usa para pacientes sin vínculo (eso es 404). 
     *  - 404: No existe o el usuario no tiene acceso: un cuidador que pide un paciente sin vínculo activo (o sus recursos) recibe 404, igual que si no existiera, para no revelar datos ajenos. 
     *
     * @param patientId 
     * @param medicationId 
     * @return [Medication]
     */
    @GET("patients/{patientId}/medications/{medicationId}")
    suspend fun getMedication(@Path("patientId") patientId: java.util.UUID, @Path("medicationId") medicationId: java.util.UUID): Response<Medication>

    /**
     * GET patients/{patientId}/medications
     * Medicamentos de un paciente
     * Por defecto solo los activos; &#x60;?includeArchived&#x3D;true&#x60; incluye los archivados. Requiere vínculo activo con el paciente (si no, 404) y consentimiento &#x60;health_data&#x60; del paciente (si no, 403 &#x60;consent_required&#x60;). 
     * Responses:
     *  - 200: Página de medicamentos.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 403: Autenticado pero sin permiso: rol que no permite la acción sobre un recurso accesible, o tipo de token equivocado (`wrong_token_type`). Nunca se usa para pacientes sin vínculo (eso es 404). 
     *  - 404: No existe o el usuario no tiene acceso: un cuidador que pide un paciente sin vínculo activo (o sus recursos) recibe 404, igual que si no existiera, para no revelar datos ajenos. 
     *  - 422: La petición no cumple el esquema.
     *
     * @param patientId 
     * @param cursor Cursor opaco devuelto como &#x60;nextCursor&#x60; en la página anterior. (optional)
     * @param limit  (optional, default to 20)
     * @param includeArchived  (optional, default to false)
     * @return [MedicationPage]
     */
    @GET("patients/{patientId}/medications")
    suspend fun listMedications(@Path("patientId") patientId: java.util.UUID, @Query("cursor") cursor: kotlin.String? = null, @Query("limit") limit: kotlin.Int? = 20, @Query("includeArchived") includeArchived: kotlin.Boolean? = false): Response<MedicationPage>

    /**
     * GET patients/{patientId}/medications/{medicationId}/schedules
     * Horarios de un medicamento
     * 
     * Responses:
     *  - 200: Horarios (sin paginar; un medicamento tiene pocos).
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 403: Autenticado pero sin permiso: rol que no permite la acción sobre un recurso accesible, o tipo de token equivocado (`wrong_token_type`). Nunca se usa para pacientes sin vínculo (eso es 404). 
     *  - 404: No existe o el usuario no tiene acceso: un cuidador que pide un paciente sin vínculo activo (o sus recursos) recibe 404, igual que si no existiera, para no revelar datos ajenos. 
     *
     * @param patientId 
     * @param medicationId 
     * @return [ScheduleList]
     */
    @GET("patients/{patientId}/medications/{medicationId}/schedules")
    suspend fun listSchedules(@Path("patientId") patientId: java.util.UUID, @Path("medicationId") medicationId: java.util.UUID): Response<ScheduleList>

    /**
     * PATCH patients/{patientId}/medications/{medicationId}
     * Editar un medicamento
     * Actualización parcial. Sube la versión del plan.
     * Responses:
     *  - 200: Medicamento actualizado.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 403: Autenticado pero sin permiso: rol que no permite la acción sobre un recurso accesible, o tipo de token equivocado (`wrong_token_type`). Nunca se usa para pacientes sin vínculo (eso es 404). 
     *  - 404: No existe o el usuario no tiene acceso: un cuidador que pide un paciente sin vínculo activo (o sus recursos) recibe 404, igual que si no existiera, para no revelar datos ajenos. 
     *  - 422: La petición no cumple el esquema.
     *
     * @param patientId 
     * @param medicationId 
     * @param medicationUpdate 
     * @return [Medication]
     */
    @PATCH("patients/{patientId}/medications/{medicationId}")
    suspend fun updateMedication(@Path("patientId") patientId: java.util.UUID, @Path("medicationId") medicationId: java.util.UUID, @Body medicationUpdate: MedicationUpdate): Response<Medication>

    /**
     * PATCH patients/{patientId}/medications/{medicationId}/schedules/{scheduleId}
     * Editar un horario
     * Actualización parcial. Sube la versión del plan. Cambiar las horas de un horario no reescribe las tomas ya registradas: el historial conserva su &#x60;scheduledAt&#x60; original. 
     * Responses:
     *  - 200: Horario actualizado.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 403: Autenticado pero sin permiso: rol que no permite la acción sobre un recurso accesible, o tipo de token equivocado (`wrong_token_type`). Nunca se usa para pacientes sin vínculo (eso es 404). 
     *  - 404: No existe o el usuario no tiene acceso: un cuidador que pide un paciente sin vínculo activo (o sus recursos) recibe 404, igual que si no existiera, para no revelar datos ajenos. 
     *  - 422: La petición no cumple el esquema.
     *
     * @param patientId 
     * @param medicationId 
     * @param scheduleId 
     * @param scheduleUpdate 
     * @return [Schedule]
     */
    @PATCH("patients/{patientId}/medications/{medicationId}/schedules/{scheduleId}")
    suspend fun updateSchedule(@Path("patientId") patientId: java.util.UUID, @Path("medicationId") medicationId: java.util.UUID, @Path("scheduleId") scheduleId: java.util.UUID, @Body scheduleUpdate: ScheduleUpdate): Response<Schedule>

}
