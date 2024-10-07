package com.vlahov.data.usecase

import com.vlahov.data.usecase.chat_connection.ChatConnectionUseCase
import com.vlahov.domain.model.Message
import com.vlahov.domain.usecase.ChatUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatUseCaseImpl @Inject constructor(
    private val chatConnectionUseCase: ChatConnectionUseCase
) : ChatUseCase {
    override val messages: Flow<Message> = chatConnectionUseCase.rawMessages

    override suspend fun sendMessage(message: String) {
        sendData(data = message.toByteArray())
    }

    // TODO: Future implementation
    override suspend fun sendData(data: ByteArray) {
        chatConnectionUseCase.writeData(data = data)
    }

    override fun close() {
        chatConnectionUseCase.closeConnection()
    }
}