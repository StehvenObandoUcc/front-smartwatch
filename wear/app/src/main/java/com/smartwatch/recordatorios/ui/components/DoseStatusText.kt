package com.smartwatch.recordatorios.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.smartwatch.recordatorios.R
import com.smartwatch.recordatorios.alarm.SnoozePolicy
import com.smartwatch.recordatorios.data.local.DoseEntity
import com.smartwatch.recordatorios.data.local.DoseStatus
import com.smartwatch.recordatorios.ui.theme.formatTime

/** "08:00 · Pendiente", "08:00 · Pospuesta 1 de 3", "08:00 · Tomada"… */
@Composable
fun doseStatusText(dose: DoseEntity): String = "${formatTime(dose.scheduledAt)} · ${doseStatusLabel(dose)}"

/** Solo el estado: "Pendiente", "Pospuesta 1 de 3", "Tomada"… */
@Composable
fun doseStatusLabel(dose: DoseEntity): String =
    when {
        dose.status == DoseStatus.TAKEN -> stringResource(R.string.status_taken)
        dose.status == DoseStatus.SKIPPED -> stringResource(R.string.status_skipped)
        dose.snoozeCount > 0 -> stringResource(R.string.snoozed_times, dose.snoozeCount, SnoozePolicy.MAX_SNOOZES)
        else -> stringResource(R.string.status_scheduled)
    }
