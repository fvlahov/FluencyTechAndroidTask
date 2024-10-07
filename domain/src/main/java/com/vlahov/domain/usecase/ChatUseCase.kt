package com.vlahov.domain.usecase

import com.vlahov.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatUseCase {
    val messages: Flow<Message>

    suspend fun sendMessage(message: String)

    suspend fun sendData(data: ByteArray)

    fun close()
}