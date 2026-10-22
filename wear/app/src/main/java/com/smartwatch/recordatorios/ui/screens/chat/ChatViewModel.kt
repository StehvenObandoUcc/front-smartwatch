package com.smartwatch.recordatorios.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartwatch.recordatorios.api.apis.ChatApi
import com.smartwatch.recordatorios.api.models.WatchChatRequest
import com.smartwatch.recordatorios.data.remote.problemCode
import com.smartwatch.recordatorios.sync.PlanRefresher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import java.util.UUID
import javax.inject.Inject

/** Lee en voz alta la respuesta (TextToSpeech en la app; se sustituye en pruebas). */
fun interface TextSpeaker {
    fun speak(text: String)
}

enum class ChatFailure { LIMIT, CONSENT, OFFLINE, UNAVAILABLE, NO_SPEECH, EXPIRED }

sealed interface ChatState {
    data object Idle : ChatState

    data object Thinking : ChatState

    /**
     * Respuesta del asistente. Con [pending] propone algo (crear un medicamento) que solo se hace si
     * el usuario lo confirma; [summary] es lo que se va a crear, en una línea.
     */
    data class Reply(
        val text: String,
        val remaining: Int,
        val pending: UUID? = null,
        val summary: String? = null,
    ) : ChatState

    data class Failure(
        val reason: ChatFailure,
    ) : ChatState
}

@HiltViewModel
class ChatViewModel
    @Inject
    constructor(
        private val api: ChatApi,
        private val speaker: TextSpeaker,
        private val plan: PlanRefresher,
    ) : ViewModel() {
        private val _state = MutableStateFlow<ChatState>(ChatState.Idle)
        val state: StateFlow<ChatState> = _state.asStateFlow()

        /** El reloj no guarda la conversación: cada pregunta va sola y la respuesta es corta. */
        fun ask(message: String) {
            if (message.isBlank() || _state.value == ChatState.Thinking) return
            _state.value = ChatState.Thinking
            viewModelScope.launch {
                _state.value = send(WatchChatRequest(message = message.trim().take(MAX_CHARS)))
                (_state.value as? ChatState.Reply)?.let { speaker.speak(it.text) }
            }
        }

        /** Sí a la propuesta: el backend crea el medicamento y el plan se descarga ya. */
        fun confirm() {
            val id = (_state.value as? ChatState.Reply)?.pending ?: return
            _state.value = ChatState.Thinking
            viewModelScope.launch {
                _state.value = send(WatchChatRequest(confirmActionId = id))
                (_state.value as? ChatState.Reply)?.let { speaker.speak(it.text) }
            }
        }

        fun speechUnavailable() {
            _state.value = ChatState.Failure(ChatFailure.NO_SPEECH)
        }

        fun reset() {
            _state.value = ChatState.Idle
        }

        private suspend fun send(request: WatchChatRequest): ChatState =
            try {
                toState(api.sendMyChatMessage(request))
            } catch (_: IOException) {
                ChatState.Failure(ChatFailure.OFFLINE)
            }

        private fun toState(response: Response<com.smartwatch.recordatorios.api.models.WatchChatReply>): ChatState {
            val body = response.body()
            return when {
                body != null -> {
                    if (!body.actions.isNullOrEmpty()) plan.refresh()
                    ChatState.Reply(
                        body.reply,
                        body.remainingMessages,
                        body.pendingAction?.id,
                        body.pendingAction?.summary,
                    )
                }
                response.code() == HTTP_NOT_FOUND && response.problemCode() == ACTION_NOT_FOUND ->
                    ChatState.Failure(ChatFailure.EXPIRED)
                response.code() == HTTP_TOO_MANY && response.problemCode() == LIMIT_REACHED ->
                    ChatState.Failure(ChatFailure.LIMIT)
                response.code() == HTTP_FORBIDDEN && response.problemCode() == CONSENT_REQUIRED ->
                    ChatState.Failure(ChatFailure.CONSENT)
                else -> ChatState.Failure(ChatFailure.UNAVAILABLE)
            }
        }

        private companion object {
            const val MAX_CHARS = 500
            const val HTTP_TOO_MANY = 429
            const val HTTP_FORBIDDEN = 403
            const val HTTP_NOT_FOUND = 404
            const val ACTION_NOT_FOUND = "chat_action_not_found"
            const val LIMIT_REACHED = "chat_limit_reached"
            const val CONSENT_REQUIRED = "consent_required"
        }
    }
