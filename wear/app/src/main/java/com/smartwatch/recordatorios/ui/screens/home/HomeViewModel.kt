package com.smartwatch.recordatorios.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartwatch.recordatorios.alarm.AlarmRescheduler
import com.smartwatch.recordatorios.alarm.DoseAlarmScheduler
import com.smartwatch.recordatorios.data.local.DoseEntity
import com.smartwatch.recordatorios.data.repository.DoseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val repository: DoseRepository,
        private val scheduler: DoseAlarmScheduler,
        private val rescheduler: AlarmRescheduler,
    ) : ViewModel() {
        val doses: StateFlow<List<DoseEntity>?> =
            repository
                .observeDoses()
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), null)

        private val _exactAlarmsAllowed = MutableStateFlow(scheduler.canScheduleExactAlarms())
        val exactAlarmsAllowed: StateFlow<Boolean> = _exactAlarmsAllowed.asStateFlow()

        init {
            viewModelScope.launch {
                repository.seedDemoIfEmpty()?.let(scheduler::scheduleAll)
            }
        }

        /** Se llama al volver a la pantalla (p. ej. desde Ajustes tras conceder el permiso). */
        fun refreshPermissions() {
            val allowed = scheduler.canScheduleExactAlarms()
            val changed = allowed != _exactAlarmsAllowed.value
            _exactAlarmsAllowed.value = allowed
            if (changed) viewModelScope.launch { rescheduler.rescheduleAll() }
        }

        fun resetDemo() {
            viewModelScope.launch { scheduler.scheduleAll(repository.seedDemoDoses()) }
        }

        private companion object {
            const val STOP_TIMEOUT_MS = 5_000L
        }
    }
