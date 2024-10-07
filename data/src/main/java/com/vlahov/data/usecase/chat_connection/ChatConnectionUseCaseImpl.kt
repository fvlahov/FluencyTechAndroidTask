package com.vlahov.data.usecase.chat_connection

import android.bluetooth.BluetoothSocket
import com.vlahov.domain.model.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject


internal class ChatConnectionUseCaseImpl @Inject constructor(

) : ChatConnectionUseCase {
    private val scope = CoroutineScope(Dispatchers.IO)

    private var socket: BluetoothSocket? = null

    private val mmBuffer: ByteArray = ByteArray(1024) // mmBuffer store for the stream

    override val rawMessages = MutableSharedFlow<Message>()

    override val onConnected = MutableSharedFlow<Unit>()

    override val onLoseConnection = MutableSharedFlow<Unit>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override fun onHostConnected(socket: BluetoothSocket) {
        this.socket = socket

        scope.launch {
            onConnected.emit(Unit)
        }

        listenForMessages()
    }

    private fun listenForMessages() = scope.launch {
        val inputStream = socket?.inputStream ?: return@launch

        // Keep listening to the InputStream until an exception occurs.
        while (true) {
            // Read from the InputStream.
            val numBytes = try {
                inputStream.read(mmBuffer)
            } catch (e: IOException) {
                onLoseConnection.tryEmit(Unit)
                Timber.e(e)
                break
            }

            val message = try {
                String(mmBuffer, 0, numBytes)
            } catch (e: Exception) {
                Timber.e(e)
                null
            }

            message?.let {
                rawMessages.emit(
                    Message(
                        id = UUID.randomUUID().toString(),
                        body = it,
                        dateTime = LocalDateTime.now(),
                        isMy = false
                    )
                )
            }
        }
    }

    override suspend fun writeData(data: ByteArray) = withContext<Unit>(Dispatchers.IO) {
        socket?.let { socket ->
            val outputStream = socket.outputStream
            try {
                outputStream.write(data)
            } catch (e: IOException) {
                onLoseConnection.tryEmit(Unit)
                Timber.e(e)
            }

            val message = String(data)
            rawMessages.emit(
                Message(
                    id = UUID.randomUUID().toString(),
                    body = message,
                    dateTime = LocalDateTime.now(),
                    isMy = true
                )
            )
            Timber.d("Successfully sent message")
        }
    }

    override fun closeConnection() {
        try {
            onLoseConnection.tryEmit(Unit)
            socket?.close()
        } catch (e: IOException) {
            Timber.e("Could not close the client socket", e)
        }

        socket = null
    }
}