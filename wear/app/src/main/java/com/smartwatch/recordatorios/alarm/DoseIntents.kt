package com.smartwatch.recordatorios.alarm

import android.content.Intent
import android.net.Uri

/**
 * Cada dosis se identifica en los Intent por su URI `dose:<id>`: así los PendingIntent de
 * distintas dosis no se pisan y se pueden cancelar sin depender de request codes.
 */
object DoseIntents {
    const val ACTION_ALARM = "com.smartwatch.recordatorios.action.DOSE_ALARM"
    const val ACTION_TAKE = "com.smartwatch.recordatorios.action.DOSE_TAKE"
    const val ACTION_SNOOZE = "com.smartwatch.recordatorios.action.DOSE_SNOOZE"

    private const val SCHEME = "dose"

    fun uriFor(doseId: String): Uri = Uri.fromParts(SCHEME, doseId, null)

    fun doseId(intent: Intent): String? = intent.data?.takeIf { it.scheme == SCHEME }?.schemeSpecificPart

    /** Id estable para la notificación de una dosis. */
    fun notificationId(doseId: String): Int = doseId.hashCode()
}
