package com.smartwatch.recordatorios.ui

import com.google.common.truth.Truth.assertThat
import com.smartwatch.recordatorios.api.apis.ChatApi
import com.smartwatch.recordatorios.api.models.ChatAction
import com.smartwatch.recordatorios.api.models.ChatRequest
import com.smartwatch.recordatorios.api.models.PendingAction
import com.smartwatch.recordatorios.api.models.WatchChatReply
import com.smartwatch.recordatorios.api.models.WatchChatRequest
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
        val sent = mutableListOf<WatchChatRequest>()

        override suspend fun sendMyChatMessage(watchChatRequest: WatchChatRequest): Response<WatchChatReply> {
            sent += watchChatRequest
            return reply()
        }

        override suspend fun sendChatMessage(
            patientId: UUID,
            chatRequest: ChatRequest,
        ): Response<String> = error("no se usa en el reloj")
    }

    private val api = FakeChatApi()
    private val spoken = mutableListOf<String>()
    private var planRefreshes = 0
    private lateinit var viewModel: ChatViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = ChatViewModel(api, { spoken += it }) { planRefreshes++ }
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
    fun `una propuesta se muestra y solo se crea al confirmar, y entonces se descarga el plan`() {
        val id = UUID.randomUUID()
        val summary = "Losartán 50 mg a las 08:00 todos los días"
        api.reply = {
            Response.success(
                WatchChatReply("¿Lo agrego?", 20, PendingAction(id, PendingAction.Type.create_medication, summary)),
            )
        }
        viewModel.ask("agrega losartán a las ocho")

        assertThat(viewModel.state.value).isEqualTo(ChatState.Reply("¿Lo agrego?", 20, id, summary))
        assertThat(planRefreshes).isEqualTo(0)

        val created = ChatAction(ChatAction.Type.medication_created, UUID.randomUUID(), UUID.randomUUID())
        api.reply = { Response.success(WatchChatReply("Listo, agregado.", 19, actions = listOf(created))) }
        viewModel.confirm()

        assertThat(api.sent.last()).isEqualTo(WatchChatRequest(confirmActionId = id))
        assertThat(viewModel.state.value).isEqualTo(ChatState.Reply("Listo, agregado.", 19))
        assertThat(planRefreshes).isEqualTo(1)
    }

    @Test
    fun `una propuesta caducada lo dice`() {
        val id = UUID.randomUUID()
        api.reply =
            {
                Response.success(
                    WatchChatReply("¿Lo agrego?", 20, PendingAction(id, PendingAction.Type.create_medication, "x")),
                )
            }
        viewModel.ask("agrega algo")
        api.reply = { problem(404, "chat_action_not_found") }

        viewModel.confirm()

        assertThat(viewModel.state.value).isEqualTo(ChatState.Failure(ChatFailure.EXPIRED))
    }

    @Test
    fun `un texto vacio no llama al backend`() {
        viewModel.ask("   ")

        assertThat(api.sent).isEmpty()
        assertThat(viewModel.state.value).isEqualTo(ChatState.Idle)
    }
}
