package com.smartwatch.recordatorios

import com.smartwatch.recordatorios.api.apis.DevicesApi
import com.smartwatch.recordatorios.api.apis.MedicationsApi
import com.smartwatch.recordatorios.api.models.Device
import com.smartwatch.recordatorios.api.models.DeviceMedicationCreate
import com.smartwatch.recordatorios.api.models.DevicePage
import com.smartwatch.recordatorios.api.models.Medication
import com.smartwatch.recordatorios.api.models.MedicationCreate
import com.smartwatch.recordatorios.api.models.MedicationPage
import com.smartwatch.recordatorios.api.models.MedicationUpdate
import com.smartwatch.recordatorios.api.models.MedicationWithSchedule
import com.smartwatch.recordatorios.api.models.MyDevice
import com.smartwatch.recordatorios.api.models.PairingCode
import com.smartwatch.recordatorios.api.models.PairingCodeRequest
import com.smartwatch.recordatorios.api.models.PairingConfirm
import com.smartwatch.recordatorios.api.models.PushTokenUpdate
import com.smartwatch.recordatorios.api.models.Schedule
import com.smartwatch.recordatorios.api.models.ScheduleCreate
import com.smartwatch.recordatorios.api.models.ScheduleList
import com.smartwatch.recordatorios.api.models.ScheduleUpdate
import com.smartwatch.recordatorios.api.models.TokenPair
import kotlinx.serialization.json.JsonObject
import retrofit2.Response
import java.util.UUID

private fun web(): Nothing = error("no se usa en el reloj")

/** `createMyMedication` responde lo que diga [create] y recuerda lo enviado; el resto es de la web. */
class FakeMedicationsApi : MedicationsApi {
    val sent = mutableListOf<DeviceMedicationCreate>()
    var create: () -> Response<MedicationWithSchedule> = { error("sin respuesta configurada") }

    override suspend fun createMyMedication(
        deviceMedicationCreate: DeviceMedicationCreate,
    ): Response<MedicationWithSchedule> {
        sent += deviceMedicationCreate
        return create()
    }

    override suspend fun archiveMedication(
        patientId: UUID,
        medicationId: UUID,
    ): Response<Unit> = web()

    override suspend fun createMedication(
        patientId: UUID,
        medicationCreate: MedicationCreate,
    ): Response<Medication> = web()

    override suspend fun createSchedule(
        patientId: UUID,
        medicationId: UUID,
        scheduleCreate: ScheduleCreate,
    ): Response<Schedule> = web()

    override suspend fun deleteSchedule(
        patientId: UUID,
        medicationId: UUID,
        scheduleId: UUID,
    ): Response<Unit> = web()

    override suspend fun getMedication(
        patientId: UUID,
        medicationId: UUID,
    ): Response<Medication> = web()

    override suspend fun listMedications(
        patientId: UUID,
        cursor: String?,
        limit: Int?,
        includeArchived: Boolean?,
    ): Response<MedicationPage> = web()

    override suspend fun listSchedules(
        patientId: UUID,
        medicationId: UUID,
    ): Response<ScheduleList> = web()

    override suspend fun updateMedication(
        patientId: UUID,
        medicationId: UUID,
        medicationUpdate: MedicationUpdate,
    ): Response<Medication> = web()

    override suspend fun updateSchedule(
        patientId: UUID,
        medicationId: UUID,
        scheduleId: UUID,
        scheduleUpdate: ScheduleUpdate,
    ): Response<Schedule> = web()
}

/** `/devices/me` del reloj vinculado: [unpair] y [me] configurables; vincular no se usa aquí. */
class FakeDeviceSessionApi : DevicesApi {
    var revoked = 0
    var unpair: () -> Response<Unit> = { Response.success(Unit) }
    var me: () -> Response<MyDevice> = { error("sin respuesta configurada") }

    override suspend fun unpairMyDevice(): Response<Unit> {
        revoked++
        return unpair()
    }

    override suspend fun getMyDevice(): Response<MyDevice> = me()

    override suspend fun confirmPairingCode(
        code: String,
        pairingConfirm: PairingConfirm,
    ): Response<Device> = web()

    override suspend fun createPairingCode(pairingCodeRequest: PairingCodeRequest): Response<PairingCode> = web()

    override suspend fun listPatientDevices(
        patientId: UUID,
        cursor: String?,
        limit: Int?,
    ): Response<DevicePage> = web()

    override suspend fun requestDeviceToken(kotlinxSerializationJsonJsonObject: JsonObject): Response<TokenPair> = web()

    override suspend fun setMyPushToken(pushTokenUpdate: PushTokenUpdate): Response<Unit> = web()

    override suspend fun unpairDevice(deviceId: UUID): Response<Unit> = web()
}
