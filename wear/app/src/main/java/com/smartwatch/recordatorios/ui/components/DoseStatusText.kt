package com.smartwatch.recordatorios.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.smartwatch.recordatorios.R
import com.smartwatch.recordatorios.data.local.DoseEntity
import com.smartwatch.recordatorios.data.local.DoseStatus
import com.smartwatch.recordatorios.ui.theme.formatTime

/** "08:00 · Pendiente", "08:00 · Pospuesta 1 de 3", "08:00 · Tomada"… */
@Composable
fun doseStatusText(dose: DoseEntity): String {
    val status =
        when {
            dose.status == DoseStatus.TAKEN -> stringResource(R.string.status_taken)
            dose.status == DoseStatus.SKIPPED -> stringResource(R.string.status_skipped)
            dose.snoozeCount > 0 -> stringResource(R.string.snoozed_times, dose.snoozeCount)
            else -> stringResource(R.string.status_scheduled)
        }
    return "${formatTime(dose.scheduledAt)} · $status"
}
