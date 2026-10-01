package com.smartwatch.recordatorios.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartwatch.recordatorios.alarm.AlarmRescheduler
import com.smartwatch.recordatorios.alarm.DoseAlarmScheduler
import com.smartwatch.recordatorios.data.local.DoseEntity
import com.smartwatch.recordatorios.data.local.DoseStatus
import com.smartwatch.recordatorios.data.remote.PlanInfo
import com.smartwatch.recordatorios.data.remote.PlanPrefs
import com.smartwatch.recordatorios.data.repository.DoseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

/** Dosis del día local `today` (la zona es la del reloj; `scheduledAt` viaja en UTC). */
fun todayDoses(
    doses: List<DoseEntity>,
    today: LocalDate,
    zone: ZoneId,
): List<DoseEntity> =
    doses.filter {
        java.time.Instant
            .ofEpochMilli(it.scheduledAt)
            .atZone(zone)
            .toLocalDate() == today
    }

/** La siguiente dosis pendiente que aún no ha sonado. */
fun nextDose(
    doses: List<DoseEntity>,
    nowMillis: Long,
): DoseEntity? =
    doses
        .filter {
            it.status == DoseStatus.SCHEDULED && it.nextAlarmAt >= nowMillis
        }.minByOrNull { it.nextAlarmAt }

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        repository: DoseRepository,
        private val scheduler: DoseAlarmScheduler,
        private val rescheduler: AlarmRescheduler,
        planPrefs: PlanPrefs,
        private val clock: Clock,
    ) : ViewModel() {
        val next: StateFlow<DoseEntity?> =
            repository
                .observeDoses()
                .map { nextDose(it, clock.millis()) }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), null)

        val today: StateFlow<List<DoseEntity>?> =
            repository
                .observeDoses()
                .map { todayDoses(it, LocalDate.now(clock.withZone(ZoneId.systemDefault())), ZoneId.systemDefault()) }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), null)

        val planInfo: StateFlow<PlanInfo> = planPrefs.info

        private val _exactAlarmsAllowed = MutableStateFlow(scheduler.canScheduleExactAlarms())
        val exactAlarmsAllowed: StateFlow<Boolean> = _exactAlarmsAllowed.asStateFlow()

        /** Se llama al volver a la pantalla (p. ej. desde Ajustes tras conceder el permiso). */
        fun refreshPermissions() {
            val allowed = scheduler.canScheduleExactAlarms()
            val changed = allowed != _exactAlarmsAllowed.value
            _exactAlarmsAllowed.value = allowed
            if (changed) viewModelScope.launch { rescheduler.rescheduleAll() }
        }

        private companion object {
            const val STOP_TIMEOUT_MS = 5_000L
        }
    }
