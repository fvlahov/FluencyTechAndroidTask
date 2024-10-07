package com.vlahov.fluencytask.presentation.chat

import androidx.compose.runtime.Immutable
import com.vlahov.domain.model.Message
import com.vlahov.fluencytask.base.UnidirectionalViewModel

interface ChatScreenContract :
    UnidirectionalViewModel<ChatScreenContract.State, ChatScreenContract.Event, ChatScreenContract.Effect> {

    @Immutable
    data class State(
        val newMessage: String = "",
        val messages: List<Message> = emptyList()
    ) {
        val canSendMessage = newMessage.isNotBlank()
    }

    sealed interface Event {
        data object OnNavigateBack : Event
        data object OnSendMessageClicked : Event
        data class OnNewMessageChanged(val value: String) : Event
    }

    sealed interface Effect {
        data object NavigateBack: Effect
    }

}