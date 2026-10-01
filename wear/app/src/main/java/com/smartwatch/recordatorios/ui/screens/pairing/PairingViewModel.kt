package com.smartwatch.recordatorios.ui.screens.pairing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartwatch.recordatorios.data.remote.PairingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PairingState {
    data object Loading : PairingState

    data class Code(
        val code: String,
    ) : PairingState

    data object Error : PairingState
}

@HiltViewModel
class PairingViewModel
    @Inject
    constructor(
        private val repository: PairingRepository,
    ) : ViewModel() {
        private val _state = MutableStateFlow<PairingState>(PairingState.Loading)
        val state: StateFlow<PairingState> = _state.asStateFlow()
        private var job: Job? = null

        init {
            start()
        }

        /** Pide un código y espera la confirmación; si caduca, pide otro. */
        fun start() {
            job?.cancel()
            job =
                viewModelScope.launch {
                    do {
                        _state.value = PairingState.Loading
                        val code = repository.requestCode()
                        if (code == null) {
                            _state.value = PairingState.Error
                            return@launch
                        }
                        _state.value = PairingState.Code(code.code)
                    } while (!repository.awaitConfirmation(code))
                }
        }
    }
