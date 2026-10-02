package com.smartwatch.recordatorios.ui.screens.chat

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.FilledTonalButton
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import com.smartwatch.recordatorios.R

/** Pregunta por voz: la respuesta se muestra corta y se lee en voz alta. */
@Composable
fun ChatScreen(
    state: ChatState,
    onAsk: () -> Unit,
) {
    val listState = rememberTransformingLazyColumnState()
    ScreenScaffold(scrollState = listState) { contentPadding ->
        TransformingLazyColumn(state = listState, contentPadding = contentPadding) {
            item { ListHeader { Text(stringResource(R.string.chat_title)) } }
            item {
                Text(
                    text = body(state),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (state is ChatState.Reply) {
                item {
                    Text(
                        text = stringResource(R.string.chat_remaining, state.remaining),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            if (state !is ChatState.Thinking) {
                item {
                    FilledTonalButton(
                        onClick = onAsk,
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(
                                stringResource(
                                    if (state is ChatState.Idle) R.string.chat_ask else R.string.chat_again,
                                ),
                            )
                        },
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
