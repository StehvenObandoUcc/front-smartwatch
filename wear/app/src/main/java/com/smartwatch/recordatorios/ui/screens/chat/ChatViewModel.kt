package com.smartwatch.recordatorios.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartwatch.recordatorios.api.apis.ChatApi
import com.smartwatch.recordatorios.api.models.ChatRequest
import com.smartwatch.recordatorios.data.remote.problemCode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

/** Lee en voz alta la respuesta (TextToSpeech en la app; se sustituye en pruebas). */
fun interface TextSpeaker {
    fun speak(text: String)
}

enum class ChatFailure { LIMIT, CONSENT, OFFLINE, UNAVAILABLE, NO_SPEECH }

sealed interface ChatState {
    data object Idle : ChatState

    data object Thinking : ChatState

    data class Reply(
        val text: String,
        val remaining: Int,
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
    ) : ViewModel() {
        private val _state = MutableStateFlow<ChatState>(ChatState.Idle)
        val state: StateFlow<ChatState> = _state.asStateFlow()

        /** El reloj no guarda la conversación: cada pregunta va sola y la respuesta es corta. */
        fun ask(message: String) {
            if (message.isBlank() || _state.value == ChatState.Thinking) return
            _state.value = ChatState.Thinking
            viewModelScope.launch {
                _state.value = send(message.trim().take(MAX_CHARS))
                (_state.value as? ChatState.Reply)?.let { speaker.speak(it.text) }
            }
        }

        fun speechUnavailable() {
            _state.value = ChatState.Failure(ChatFailure.NO_SPEECH)
        }

        fun reset() {
            _state.value = ChatState.Idle
        }

        private suspend fun send(message: String): ChatState =
            try {
                toState(api.sendMyChatMessage(ChatRequest(message)))
            } catch (_: IOException) {
                ChatState.Failure(ChatFailure.OFFLINE)
            }

        private fun toState(response: Response<com.smartwatch.recordatorios.api.models.WatchChatReply>): ChatState {
            val body = response.body()
            return when {
                body != null -> ChatState.Reply(body.reply, body.remainingMessages)
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
            const val LIMIT_REACHED = "chat_limit_reached"
            const val CONSENT_REQUIRED = "consent_required"
        }
    }
