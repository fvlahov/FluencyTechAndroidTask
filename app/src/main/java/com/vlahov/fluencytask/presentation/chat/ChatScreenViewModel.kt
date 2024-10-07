package com.vlahov.fluencytask.presentation.chat

import com.vlahov.domain.usecase.ChatUseCase
import com.vlahov.fluencytask.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ChatScreenViewModel @Inject constructor(
    private val chatUseCase: ChatUseCase
) : BaseViewModel(), ChatScreenContract {

    private val mutableState = MutableStateFlow(ChatScreenContract.State())
    override val state: StateFlow<ChatScreenContract.State> = mutableState.asStateFlow()

    private val mutableEffect = Channel<ChatScreenContract.Effect>()
    override val effect = mutableEffect.receiveAsFlow()


    override fun onEvent(event: ChatScreenContract.Event) {
        when (event) {
            ChatScreenContract.Event.OnNavigateBack -> mutableEffect.trySend(ChatScreenContract.Effect.NavigateBack)
            is ChatScreenContract.Event.OnNewMessageChanged -> mutableState.update { it.copy(newMessage = event.value) }
            ChatScreenContract.Event.OnSendMessageClicked -> sendMessage()
        }
    }

    init {
        launchIn {
            chatUseCase.messages.collectLatest { message ->
                mutableState.update { it.copy(messages = it.messages + message) }
            }
        }
    }

    private fun sendMessage() {
        launchWithProgressIn {
            chatUseCase.sendMessage(state.value.newMessage)
            mutableState.update { it.copy(newMessage = "") }
        }
    }

    override fun onCleared() {
        super.onCleared()
        chatUseCase.close()
    }
}
