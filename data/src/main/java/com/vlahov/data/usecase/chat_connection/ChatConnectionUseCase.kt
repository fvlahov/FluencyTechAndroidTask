package com.vlahov.data.usecase.chat_connection

import android.bluetooth.BluetoothSocket
import com.vlahov.domain.model.Message
import kotlinx.coroutines.flow.SharedFlow

interface ChatConnectionUseCase {
    val rawMessages : SharedFlow<Message>
    val onConnected: SharedFlow<Unit>
    val onLoseConnection: SharedFlow<Unit>

    val errors: SharedFlow<ChatError>

    fun onHostConnected(socket: BluetoothSocket)

    suspend fun writeData(data: ByteArray)

    fun closeConnection()
}