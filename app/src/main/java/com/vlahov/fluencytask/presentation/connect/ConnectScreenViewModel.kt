package com.vlahov.fluencytask.presentation.connect

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.widget.Toast
import com.vlahov.fluencytask.R
import com.vlahov.fluencytask.base.BaseViewModel
import com.vlahov.fluencytask.service.ConnectToChatUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ConnectScreenViewModel @Inject constructor(
    private val connectToChatUseCase: ConnectToChatUseCase,
    @ApplicationContext private val context: Context
) : BaseViewModel(), ConnectScreenContract {

    private val mutableState = MutableStateFlow(ConnectScreenContract.State())
    override val state: StateFlow<ConnectScreenContract.State> = mutableState.asStateFlow()

    private val mutableEffect = Channel<ConnectScreenContract.Effect>()
    override val effect = mutableEffect.receiveAsFlow()

    override fun onEvent(event: ConnectScreenContract.Event) {
        when (event) {
            ConnectScreenContract.Event.OnBackClicked -> mutableEffect.trySend(ConnectScreenContract.Effect.NavigateBack)
            is ConnectScreenContract.Event.OnBluetoothEnableResult -> {
                if (event.enabled)
                    startDiscoveringBluetoothDevices()
                mutableState.update { it.copy(bluetoothEnabled = event.enabled) }
            }
            is ConnectScreenContract.Event.OnPermissionResult -> {
                mutableState.update { it.copy(hasBluetoothPermission = event.granted) }
            }

            is ConnectScreenContract.Event.OnDeviceClicked -> connectToChat(event.device)
        }
    }

    // We're calling this function ONLY after granting bluetooth permission
    @SuppressLint("MissingPermission")
    private fun startDiscoveringBluetoothDevices() {
        launchIn {
            connectToChatUseCase.getBluetoothDevicesFlow().collect { device ->
                if(mutableState.value.devices.contains(device).not() && device.name.isNullOrEmpty().not())
                    mutableState.update { it.copy(devices = it.devices + device) }
            }
        }
    }

    // We're calling this function ONLY after granting bluetooth permission
    @SuppressLint("MissingPermission")
    private fun connectToChat(device: BluetoothDevice) {
        launchWithProgressIn {
            try {
                connectToChatUseCase.connectToDevice(device)
            } catch (e: IOException) {
                Timber.e(e)
                Toast.makeText(context, context.getString(R.string.connection_failed), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
