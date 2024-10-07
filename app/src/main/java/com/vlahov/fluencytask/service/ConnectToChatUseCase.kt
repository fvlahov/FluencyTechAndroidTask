package com.vlahov.fluencytask.service

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.annotation.RequiresPermission
import com.vlahov.data.usecase.chat_connection.ChatConnectionUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID
import javax.inject.Inject

class ConnectToChatUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val chatConnectionUseCase: ChatConnectionUseCase
) {
    private val bluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val adapter = bluetoothManager.adapter

    /**
     * @return [Flow] of [BluetoothDevice], to stop discovery, cancel the collecting coroutine scope
     */
    @RequiresPermission(
        allOf = [
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.BLUETOOTH_SCAN,
        ]
    )
    fun getBluetoothDevicesFlow(): Flow<BluetoothDevice> = callbackFlow {

        // First send paired devices
        adapter.bondedDevices.forEach { bluetoothDevice ->
            this.trySend(bluetoothDevice)
        }

        adapter.startDiscovery()

        // Discover bluetooth devices
        val receiver = DeviceReceiver(onDeviceReceived = ::trySend)

        context.registerReceiver(receiver, IntentFilter(BluetoothDevice.ACTION_FOUND))

        // Cleanup
        awaitClose {
            context.unregisterReceiver(receiver)
            adapter.cancelDiscovery()
            close()
        }
    }

    @Throws(IOException::class)
    @RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
    suspend fun connectToDevice(device: BluetoothDevice) = withContext(Dispatchers.IO) {
        val socket = device.createRfcommSocketToServiceRecord(
            UUID.fromString("7cade330-ce41-418a-98cf-46246e2eebcb")
        )
        socket?.let {
            socket.connect() //Blocks
            chatConnectionUseCase.onHostConnected(socket)
        }
    }

    private class DeviceReceiver(private val onDeviceReceived: (BluetoothDevice) -> Unit) :
        BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and send it via onDeviceReceived
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                        ?.also(onDeviceReceived)
                }
            }
        }
    }
}