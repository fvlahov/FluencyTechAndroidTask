package com.vlahov.fluencytask.presentation.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.vlahov.fluencytask.R
import com.vlahov.fluencytask.base.collectInLaunchedEffect
import com.vlahov.fluencytask.base.screenViewModel
import com.vlahov.fluencytask.presentation.chat.shared.ChatMessage
import com.vlahov.fluencytask.ui.layout.BaseScreenWithTopBar
import com.vlahov.fluencytask.ui.theme.FluencyTaskTheme
import com.vlahov.fluencytask.ui.theme.LocalScreenPadding
import com.vlahov.fluencytask.ui.theme.LocalSpacing
import com.vlahov.fluencytask.utils.dummyMessages
import kotlinx.serialization.Serializable

@Serializable
object ChatScreen

fun NavGraphBuilder.chatScreen(
    onNavigateBack: () -> Unit,
) {
    composable<ChatScreen> {
        val viewModel = screenViewModel<ChatScreenViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()

        viewModel.effect.collectInLaunchedEffect { effect ->
            when (effect) {
                ChatScreenContract.Effect.NavigateBack -> onNavigateBack()
            }
        }

        ChatScreen(
            state = state,
            onEvent = viewModel::onEvent,
        )
    }
}

@Composable
private fun ChatScreen(
    state: ChatScreenContract.State,
    onEvent: (ChatScreenContract.Event) -> Unit,
) {
    BaseScreenWithTopBar(
        title = stringResource(id = R.string.chat),
        onBackClicked = { onEvent(ChatScreenContract.Event.OnNavigateBack) },
        modifier = Modifier
            .padding(
                horizontal = LocalScreenPadding.current
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(LocalSpacing.current),
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                TextField(
                    value = state.newMessage,
                    onValueChange = { onEvent(ChatScreenContract.Event.OnNewMessageChanged(it)) },
                    placeholder = {
                        Text(text = stringResource(id = R.string.your_message))
                    },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    trailingIcon = {
                        IconButton(
                            enabled = state.canSendMessage,
                            onClick = { onEvent(ChatScreenContract.Event.OnSendMessageClicked) }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = stringResource(id = R.string.send_message)
                            )
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(LocalSpacing.current),
            modifier = Modifier
                .weight(1f)
        ) {
            if (state.messages.isEmpty())
                item(key = "Empty state") {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(LocalSpacing.current),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = null)
                        Text(text = stringResource(id = R.string.no_messages_yet))
                    }
                }

            items(state.messages, key = { it.id }) { message ->
                Row(
                    horizontalArrangement = if(message.isMy)
                        Arrangement.End
                    else
                        Arrangement.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem()
                ) {
                    ChatMessage(
                        message = message,
                        modifier = Modifier.fillMaxWidth(0.75f)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    FluencyTaskTheme {
        ChatScreen(
            state = ChatScreenContract.State(
                messages = dummyMessages
            ),
            onEvent = { }
        )
    }
}