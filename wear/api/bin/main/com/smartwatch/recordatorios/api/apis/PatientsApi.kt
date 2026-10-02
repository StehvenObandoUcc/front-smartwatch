package com.smartwatch.recordatorios.api.apis

import com.smartwatch.recordatorios.api.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import com.smartwatch.recordatorios.api.models.CareLink
import com.smartwatch.recordatorios.api.models.CareLinkPage
import com.smartwatch.recordatorios.api.models.CaregiverInvitation
import com.smartwatch.recordatorios.api.models.Consent
import com.smartwatch.recordatorios.api.models.ConsentList
import com.smartwatch.recordatorios.api.models.ConsentPurpose
import com.smartwatch.recordatorios.api.models.ConsentUpdate
import com.smartwatch.recordatorios.api.models.Patient
import com.smartwatch.recordatorios.api.models.PatientCreate
import com.smartwatch.recordatorios.api.models.PatientPage
import com.smartwatch.recordatorios.api.models.PatientUpdate
import com.smartwatch.recordatorios.api.models.Problem
import com.smartwatch.recordatorios.api.models.ValidationProblem

interface PatientsApi {
    /**
     * POST caregiver-invitations/{code}/accept
     * Aceptar una invitación
     * Solo cuidadores (un paciente recibe 403). Crea o reactiva el vínculo y consume el código. Un código inexistente, caducado o ya usado responde 404 (&#x60;code: invitation_not_found&#x60;). Límite de intentos por usuario e IP (429) para impedir adivinar códigos. Si el cuidador ya tiene un vínculo activo con ese paciente: 409 (&#x60;code: already_linked&#x60;). 
     * Responses:
     *  - 201: Vínculo activo.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 403: Autenticado pero sin permiso: rol que no permite la acción sobre un recurso accesible, o tipo de token equivocado (`wrong_token_type`). Nunca se usa para pacientes sin vínculo (eso es 404). 
     *  - 404: No existe o el usuario no tiene acceso: un cuidador que pide un paciente sin vínculo activo (o sus recursos) recibe 404, igual que si no existiera, para no revelar datos ajenos. 
     *  - 409: Conflicto con el estado actual (por ejemplo, correo ya registrado).
     *  - 429: Límite de peticiones superado.
     *
     * @param code 
     * @return [CareLink]
     */
    @POST("caregiver-invitations/{code}/accept")
    suspend fun acceptCaregiverInvitation(@Path("code") code: kotlin.String): Response<CareLink>

    /**
     * POST patients/{patientId}/caregiver-invitations
     * Invitar a un cuidador
     * Genera un código de un solo uso, válido 72 h, que se comparte a mano con el cuidador (el envío por correo llega en la fase 4). Lo crea el paciente con cuenta o, en un paciente gestionado, un cuidador con vínculo activo. Límite de invitaciones por paciente y día (429). 
     * Responses:
     *  - 201: Invitación creada.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 403: Autenticado pero sin permiso: rol que no permite la acción sobre un recurso accesible, o tipo de token equivocado (`wrong_token_type`). Nunca se usa para pacientes sin vínculo (eso es 404). 
     *  - 404: No existe o el usuario no tiene acceso: un cuidador que pide un paciente sin vínculo activo (o sus recursos) recibe 404, igual que si no existiera, para no revelar datos ajenos. 
     *  - 429: Límite de peticiones superado.
     *
     * @param patientId 
     * @return [CaregiverInvitation]
     */
    @POST("patients/{patientId}/caregiver-invitations")
    suspend fun createCaregiverInvitation(@Path("patientId") patientId: java.util.UUID): Response<CaregiverInvitation>

    /**
     * POST patients
     * Crear paciente gestionado
     * Solo cuidadores. Crea un paciente sin cuenta propia (por ejemplo, una persona mayor que solo usará el reloj) y un vínculo activo con el cuidador que lo crea. 
     * Responses:
     *  - 201: Paciente creado.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 403: Autenticado pero sin permiso: rol que no permite la acción sobre un recurso accesible, o tipo de token equivocado (`wrong_token_type`). Nunca se usa para pacientes sin vínculo (eso es 404). 
     *  - 422: La petición no cumple el esquema.
     *
     * @param patientCreate 
     * @return [Patient]
     */
    @POST("patients")
    suspend fun createPatient(@Body patientCreate: PatientCreate): Response<Patient>

    /**
     * GET patients/{patientId}
     * Detalle de un paciente
     * 
     * Responses:
     *  - 200: Paciente.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 404: No existe o el usuario no tiene acceso: un cuidador que pide un paciente sin vínculo activo (o sus recursos) recibe 404, igual que si no existiera, para no revelar datos ajenos. 
     *
     * @param patientId 
     * @return [Patient]
     */
    @GET("patients/{patientId}")
    suspend fun getPatient(@Path("patientId") patientId: java.util.UUID): Response<Patient>

    /**
     * GET patients/{patientId}/caregivers
     * Cuidadores de un paciente
     * 
     * Responses:
     *  - 200: Página de vínculos activos.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 404: No existe o el usuario no tiene acceso: un cuidador que pide un paciente sin vínculo activo (o sus recursos) recibe 404, igual que si no existiera, para no revelar datos ajenos. 
     *  - 422: La petición no cumple el esquema.
     *
     * @param patientId 
     * @param cursor Cursor opaco devuelto como &#x60;nextCursor&#x60; en la página anterior. (optional)
     * @param limit  (optional, default to 20)
     * @return [CareLinkPage]
     */
    @GET("patients/{patientId}/caregivers")
    suspend fun listPatientCaregivers(@Path("patientId") patientId: java.util.UUID, @Query("cursor") cursor: kotlin.String? = null, @Query("limit") limit: kotlin.Int? = 20): Response<CareLinkPage>

    /**
     * GET patients/{patientId}/consents
     * Consentimientos de un paciente
     * Lo pueden leer el propio paciente y sus cuidadores con vínculo activo. Devuelve siempre las tres finalidades; las no respondidas llevan &#x60;granted &#x3D; false&#x60;. 
     * Responses:
     *  - 200: Estado de cada finalidad.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 404: No existe o el usuario no tiene acceso: un cuidador que pide un paciente sin vínculo activo (o sus recursos) recibe 404, igual que si no existiera, para no revelar datos ajenos. 
     *
     * @param patientId 
     * @return [ConsentList]
     */
    @GET("patients/{patientId}/consents")
    suspend fun listPatientConsents(@Path("patientId") patientId: java.util.UUID): Response<ConsentList>

    /**
     * GET patients
     * Pacientes accesibles
     * Un paciente ve solo su propio perfil. Un cuidador ve los pacientes con vínculo activo y los que él mismo gestiona. 
     * Responses:
     *  - 200: Página de pacientes.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 422: La petición no cumple el esquema.
     *
     * @param cursor Cursor opaco devuelto como &#x60;nextCursor&#x60; en la página anterior. (optional)
     * @param limit  (optional, default to 20)
     * @return [PatientPage]
     */
    @GET("patients")
    suspend fun listPatients(@Query("cursor") cursor: kotlin.String? = null, @Query("limit") limit: kotlin.Int? = 20): Response<PatientPage>

    /**
     * DELETE patients/{patientId}/caregivers/{userId}
     * Revocar el vínculo de un cuidador
     * Desactiva el vínculo (no se borra: queda para auditoría) y el cuidador deja de ver al paciente de inmediato. Pueden hacerlo: - el paciente con cuenta, sobre cualquiera de sus cuidadores; - el propio cuidador, sobre su vínculo; - en un paciente gestionado, cualquier cuidador con vínculo activo sobre otro, salvo el   último (409 &#x60;code: last_caregiver&#x60;, el paciente quedaría sin nadie que lo gestione). Idempotente: revocar un vínculo ya inactivo devuelve 204. 
     * Responses:
     *  - 204: Vínculo inactivo.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 403: Autenticado pero sin permiso: rol que no permite la acción sobre un recurso accesible, o tipo de token equivocado (`wrong_token_type`). Nunca se usa para pacientes sin vínculo (eso es 404). 
     *  - 404: No existe o el usuario no tiene acceso: un cuidador que pide un paciente sin vínculo activo (o sus recursos) recibe 404, igual que si no existiera, para no revelar datos ajenos. 
     *  - 409: Conflicto con el estado actual (por ejemplo, correo ya registrado).
     *
     * @param patientId 
     * @param userId Id de usuario del cuidador.
     * @return [Unit]
     */
    @DELETE("patients/{patientId}/caregivers/{userId}")
    suspend fun revokePatientCaregiver(@Path("patientId") patientId: java.util.UUID, @Path("userId") userId: java.util.UUID): Response<Unit>

    /**
     * PUT patients/{patientId}/consents/{purpose}
     * Otorgar o retirar un consentimiento del paciente
     * - Paciente con cuenta: solo él (equivale a &#x60;PUT /users/me/consents/{purpose}&#x60;); un   cuidador recibe 403. - Paciente gestionado: cualquier cuidador con vínculo activo, en nombre del paciente. Idempotente. Cada cambio queda en el historial con fecha, versión del texto y &#x60;grantedBy&#x60; (usuario que decidió). 
     * Responses:
     *  - 200: Consentimiento actualizado.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 403: Autenticado pero sin permiso: rol que no permite la acción sobre un recurso accesible, o tipo de token equivocado (`wrong_token_type`). Nunca se usa para pacientes sin vínculo (eso es 404). 
     *  - 404: No existe o el usuario no tiene acceso: un cuidador que pide un paciente sin vínculo activo (o sus recursos) recibe 404, igual que si no existiera, para no revelar datos ajenos. 
     *  - 422: La petición no cumple el esquema.
     *
     * @param patientId 
     * @param purpose 
     * @param consentUpdate 
     * @return [Consent]
     */
    @PUT("patients/{patientId}/consents/{purpose}")
    suspend fun setPatientConsent(@Path("patientId") patientId: java.util.UUID, @Path("purpose") purpose: ConsentPurpose, @Body consentUpdate: ConsentUpdate): Response<Consent>

    /**
     * PATCH patients/{patientId}
     * Actualizar un paciente
     * Un paciente con cuenta edita su propio perfil. Un paciente gestionado lo editan sus cuidadores con vínculo activo. Un cuidador sobre un paciente con cuenta recibe 403. 
     * Responses:
     *  - 200: Paciente actualizado.
     *  - 401: Falta el token, es inválido o caducó (o credenciales incorrectas en login).
     *  - 403: Autenticado pero sin permiso: rol que no permite la acción sobre un recurso accesible, o tipo de token equivocado (`wrong_token_type`). Nunca se usa para pacientes sin vínculo (eso es 404). 
     *  - 404: No existe o el usuario no tiene acceso: un cuidador que pide un paciente sin vínculo activo (o sus recursos) recibe 404, igual que si no existiera, para no revelar datos ajenos. 
     *  - 422: La petición no cumple el esquema.
     *
     * @param patientId 
     * @param patientUpdate 
     * @return [Patient]
     */
    @PATCH("patients/{patientId}")
    suspend fun updatePatient(@Path("patientId") patientId: java.util.UUID, @Body patientUpdate: PatientUpdate): Response<Patient>

}
