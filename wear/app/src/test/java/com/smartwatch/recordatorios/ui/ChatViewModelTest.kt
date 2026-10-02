package com.smartwatch.recordatorios.ui

import com.google.common.truth.Truth.assertThat
import com.smartwatch.recordatorios.api.apis.ChatApi
import com.smartwatch.recordatorios.api.models.ChatRequest
import com.smartwatch.recordatorios.api.models.WatchChatReply
import com.smartwatch.recordatorios.ui.screens.chat.ChatFailure
import com.smartwatch.recordatorios.ui.screens.chat.ChatState
import com.smartwatch.recordatorios.ui.screens.chat.ChatViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {
    private class FakeChatApi : ChatApi {
        var reply: () -> Response<WatchChatReply> = { error("sin respuesta configurada") }
        val sent = mutableListOf<ChatRequest>()

        override suspend fun sendMyChatMessage(chatRequest: ChatRequest): Response<WatchChatReply> {
            sent += chatRequest
            return reply()
        }

        override suspend fun sendChatMessage(
            patientId: UUID,
            chatRequest: ChatRequest,
        ): Response<String> = error("no se usa en el reloj")
    }

    private val api = FakeChatApi()
    private val spoken = mutableListOf<String>()
    private lateinit var viewModel: ChatViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = ChatViewModel(api) { spoken += it }
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun problem(
        status: Int,
        code: String,
    ): Response<WatchChatReply> =
        Response.error(status, """{"code":"$code"}""".toResponseBody("application/problem+json".toMediaType()))

    @Test
    fun `la respuesta se muestra y se lee en voz alta`() {
        api.reply = { Response.success(WatchChatReply("Te toca la metformina a las 20:00.", 26)) }

        viewModel.ask("  ¿qué me toca esta noche?  ")

        assertThat(viewModel.state.value).isEqualTo(ChatState.Reply("Te toca la metformina a las 20:00.", 26))
        assertThat(spoken).containsExactly("Te toca la metformina a las 20:00.")
        assertThat(api.sent.single().message).isEqualTo("¿qué me toca esta noche?")
    }

    @Test
    fun `429 chat_limit_reached avisa del limite diario y no habla`() {
        api.reply = { problem(429, "chat_limit_reached") }

        viewModel.ask("hola")

        assertThat(viewModel.state.value).isEqualTo(ChatState.Failure(ChatFailure.LIMIT))
        assertThat(spoken).isEmpty()
    }

    @Test
    fun `403 consent_required pide aceptar el asistente en la web`() {
        api.reply = { problem(403, "consent_required") }

        viewModel.ask("hola")

        assertThat(viewModel.state.value).isEqualTo(ChatState.Failure(ChatFailure.CONSENT))
    }

    @Test
    fun `sin red o con error del servidor se informa sin romperse`() {
        api.reply = { throw IOException("sin red") }
        viewModel.ask("hola")
        assertThat(viewModel.state.value).isEqualTo(ChatState.Failure(ChatFailure.OFFLINE))

        api.reply = { problem(500, "internal_error") }
        viewModel.ask("hola")
        assertThat(viewModel.state.value).isEqualTo(ChatState.Failure(ChatFailure.UNAVAILABLE))
    }

    @Test
    fun `un texto vacio no llama al backend`() {
        viewModel.ask("   ")

        assertThat(api.sent).isEmpty()
        assertThat(viewModel.state.value).isEqualTo(ChatState.Idle)
    }
}
