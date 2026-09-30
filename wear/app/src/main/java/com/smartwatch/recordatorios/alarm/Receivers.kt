package com.smartwatch.recordatorios.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.CallSuper
import com.smartwatch.recordatorios.data.local.DoseAction
import com.smartwatch.recordatorios.di.ApplicationScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Hilt inyecta en `super.onReceive`; esta base lo hace posible con `BroadcastReceiver`. */
abstract class HiltBroadcastReceiver : BroadcastReceiver() {
    @CallSuper
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) = Unit
}

/** Mantiene vivo el receiver mientras termina el trabajo en segundo plano. */
private fun BroadcastReceiver.runAsync(
    scope: CoroutineScope,
    block: suspend () -> Unit,
) {
    val pending = goAsync()
    scope.launch {
        try {
            block()
        } finally {
            pending.finish()
        }
    }
}

/** Suena la alarma: muestra la alerta de la dosis si sigue pendiente. */
@AndroidEntryPoint
class DoseAlarmReceiver : HiltBroadcastReceiver() {
    @Inject lateinit var firing: DoseAlarmFiring

    @Inject @ApplicationScope
    lateinit var scope: CoroutineScope

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        super.onReceive(context, intent)
        val doseId = DoseIntents.doseId(intent) ?: return
        if (intent.action != DoseIntents.ACTION_ALARM) return
        runAsync(scope) { firing.fire(doseId) }
    }
}

/** Acciones desde la notificación: Tomada y Posponer. */
@AndroidEntryPoint
class DoseActionReceiver : HiltBroadcastReceiver() {
    @Inject lateinit var handler: DoseActionHandler

    @Inject @ApplicationScope
    lateinit var scope: CoroutineScope

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        super.onReceive(context, intent)
        val doseId = DoseIntents.doseId(intent) ?: return
        val action =
            when (intent.action) {
                DoseIntents.ACTION_TAKE -> DoseAction.TAKEN
                DoseIntents.ACTION_SNOOZE -> DoseAction.SNOOZED
                else -> return
            }
        runAsync(scope) { handler.handle(doseId, action) }
    }
}

/** Tras reiniciar, actualizar la app o conceder el permiso de alarmas exactas, reprograma todo. */
@AndroidEntryPoint
class BootReceiver : HiltBroadcastReceiver() {
    @Inject lateinit var rescheduler: AlarmRescheduler

    @Inject @ApplicationScope
    lateinit var scope: CoroutineScope

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        super.onReceive(context, intent)
        if (intent.action !in HANDLED_ACTIONS) return
        runAsync(scope) { rescheduler.rescheduleAll() }
    }

    companion object {
        val HANDLED_ACTIONS =
            setOf(
                Intent.ACTION_BOOT_COMPLETED,
                Intent.ACTION_MY_PACKAGE_REPLACED,
                android.app.AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED,
            )
    }
}
