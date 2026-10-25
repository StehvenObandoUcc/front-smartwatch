package com.smartwatch.recordatorios.data.remote

import android.content.Context
import android.os.BatteryManager
import com.smartwatch.recordatorios.api.apis.DevicesApi
import com.smartwatch.recordatorios.api.models.BatteryUpdate
import com.smartwatch.recordatorios.di.DeviceSessionApi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Lo que el reloj vinculado sabe de sí mismo (`GET /devices/me`): el nombre del paciente, para decir
 * «Vinculado con Marta». También revoca sus propios tokens al desvincular (`DELETE /devices/me`).
 */
@Singleton
class DeviceSession
    @Inject
    constructor(
        @param:DeviceSessionApi private val api: DevicesApi,
        @param:ApplicationContext private val context: Context,
    ) {
        private val prefs = context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
        private val _patientName = MutableStateFlow(prefs.getString(KEY_NAME, null))

        val patientName: StateFlow<String?> = _patientName.asStateFlow()

        /** Sin red no pasa nada: se queda el nombre guardado y se reintenta en la próxima sincronización. */
        suspend fun refresh() {
            val name =
                try {
                    api.getMyDevice().body()?.patientDisplayName
                } catch (_: IOException) {
                    null
                } ?: return
            prefs.edit().putString(KEY_NAME, name).apply()
            _patientName.value = name
        }

        /** Informa la batería para que el cuidador la vea en la web. Sin red, en la próxima sincronización. */
        suspend fun reportBattery() {
            val level =
                context
                    .getSystemService(BatteryManager::class.java)
                    ?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            if (level == null || level !in 0..FULL) return
            try {
                api.setMyBattery(BatteryUpdate(level))
            } catch (_: IOException) {
                // Sin red: se reintenta en la próxima sincronización.
            }
        }

        /**
         * Revoca los tokens en el backend para que el reloj deje de aparecer en la web. Si no hay red
         * se desvincula igual en el reloj; el cuidador puede quitarlo desde la web.
         */
        suspend fun revoke() {
            try {
                api.unpairMyDevice()
            } catch (_: IOException) {
                // Sin red: el reloj se desvincula localmente de todos modos.
            }
            clear()
        }

        fun clear() {
            prefs.edit().clear().apply()
            _patientName.value = null
        }

        private companion object {
            const val FILE = "device"
            const val KEY_NAME = "patient_name"
            const val FULL = 100
        }
    }
