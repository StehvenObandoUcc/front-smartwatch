package com.smartwatch.recordatorios.ui.screens.chat

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import com.smartwatch.recordatorios.R
import com.smartwatch.recordatorios.ui.components.BwHeader
import com.smartwatch.recordatorios.ui.components.BwMicButton
import com.smartwatch.recordatorios.ui.components.BwScreenScaffold
import com.smartwatch.recordatorios.ui.components.BwText
import com.smartwatch.recordatorios.ui.theme.BwTheme

/** Pregunta por voz: la respuesta se muestra corta y se lee en voz alta. */
@Composable
fun ChatScreen(
    state: ChatState,
    onAsk: () -> Unit,
) {
    val listState = rememberTransformingLazyColumnState()
    BwScreenScaffold(scrollState = listState) { contentPadding ->
        TransformingLazyColumn(state = listState, contentPadding = contentPadding) {
            item { BwHeader(stringResource(R.string.chat_title)) }
            item {
                BwText(
                    text = body(state),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (state is ChatState.Reply) {
                item {
                    BwText(
                        text = stringResource(R.string.chat_remaining, state.remaining),
                        style = BwTheme.type.support,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            if (state !is ChatState.Thinking) {
                item {
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
                },
            )
    }
