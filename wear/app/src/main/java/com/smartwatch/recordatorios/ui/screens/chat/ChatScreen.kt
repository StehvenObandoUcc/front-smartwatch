package com.smartwatch.recordatorios.ui.screens.chat

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import com.smartwatch.recordatorios.R
import com.smartwatch.recordatorios.ui.components.BwActionPill
import com.smartwatch.recordatorios.ui.components.BwButton
import com.smartwatch.recordatorios.ui.components.BwCard
import com.smartwatch.recordatorios.ui.components.BwHeader
import com.smartwatch.recordatorios.ui.components.BwMicButton
import com.smartwatch.recordatorios.ui.components.BwScreenScaffold
import com.smartwatch.recordatorios.ui.components.BwText
import com.smartwatch.recordatorios.ui.components.edgeItem
import com.smartwatch.recordatorios.ui.theme.BwTheme

/**
 * Pregunta por voz: la respuesta se muestra corta y se lee en voz alta. Si el asistente propone crear
 * un medicamento, se ve qué va a crear y solo se hace con «Sí, agregar» (la opción principal); «No»
 * la descarta.
 */
@Composable
fun ChatScreen(
    state: ChatState,
    onAsk: () -> Unit,
    onConfirm: () -> Unit = {},
    onDecline: () -> Unit = {},
) {
    val proposal = (state as? ChatState.Reply)?.takeIf { it.pending != null }
    val listState = rememberTransformingLazyColumnState()
    BwScreenScaffold(scrollState = listState) { contentPadding ->
        TransformingLazyColumn(state = listState, contentPadding = contentPadding) {
            edgeItem { BwHeader(stringResource(R.string.chat_title), icon = R.drawable.ic_mic) }
            edgeItem {
                BwText(
                    text = body(state),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            proposal?.summary?.let { summary ->
                edgeItem(container = true) {
                    BwCard(onClick = onConfirm, modifier = Modifier.fillMaxWidth()) {
                        BwText(summary, style = BwTheme.type.label, textAlign = TextAlign.Center)
                    }
                }
            }
            if (proposal != null) {
                edgeItem {
                    BwButton(
                        label = stringResource(R.string.chat_confirm_yes),
                        onClick = onConfirm,
                        primary = true,
                        icon = R.drawable.ic_check,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                edgeItem(container = true) {
                    BwActionPill(
                        label = stringResource(R.string.chat_confirm_no),
                        icon = R.drawable.ic_close,
                        onClick = onDecline,
                        quiet = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            if (state is ChatState.Reply) {
                edgeItem {
                    BwText(
                        text = stringResource(R.string.chat_remaining, state.remaining),
                        style = BwTheme.type.support,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            if (state !is ChatState.Thinking && proposal == null) {
                edgeItem(container = true) {
                    BwMicButton(
                        label =
                            stringResource(
                                if (state is ChatState.Idle) R.string.chat_ask else R.string.chat_again,
                            ),
                        onClick = onAsk,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun body(state: ChatState): String =
    when (state) {
        ChatState.Idle -> stringResource(R.string.chat_prompt)
        ChatState.Thinking -> stringResource(R.string.chat_thinking)
        is ChatState.Reply -> state.text
        is ChatState.Failure ->
            stringResource(
                when (state.reason) {
                    ChatFailure.LIMIT -> R.string.chat_error_limit
                    ChatFailure.CONSENT -> R.string.chat_error_consent
                    ChatFailure.OFFLINE -> R.string.chat_error_offline
                    ChatFailure.UNAVAILABLE -> R.string.chat_error_unavailable
                    ChatFailure.NO_SPEECH -> R.string.chat_error_no_speech
                    ChatFailure.EXPIRED -> R.string.chat_error_expired
                },
            )
    }
