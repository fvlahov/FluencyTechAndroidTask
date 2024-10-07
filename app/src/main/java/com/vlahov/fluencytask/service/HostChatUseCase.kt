package com.vlahov.fluencytask.service

import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import androidx.annotation.RequiresPermission
import com.vlahov.data.usecase.chat_connection.ChatConnectionUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import java.util.UUID
import javax.inject.Inject

class HostChatUseCase @Inject constructor(
    @ApplicationContext context: Context,
    private val chatConnectionUseCase: ChatConnectionUseCase
) {
    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val adapter = bluetoothManager.adapter

    @RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
    suspend fun awaitConnection() = withContext(Dispatchers.IO) {
        val serverSocket by lazy(LazyThreadSafetyMode.NONE) {
            adapter?.listenUsingInsecureRfcommWithServiceRecord(
                "MySuperDuperName",
                UUID.fromString("7cade330-ce41-418a-98cf-46246e2eebcb")
            )
        }

        Timber.d("Waiting connection")

        var socket: BluetoothSocket? = null

        while (socket == null) {
            socket = try {
                serverSocket?.accept()
            } catch (e: IOException) {
                Timber.e("Socket's accept() method failed", e)
                null
            }
        }

        chatConnectionUseCase.onHostConnected(socket)

        Timber.d("Connection successful")


        serverSocket?.close()
    }
}