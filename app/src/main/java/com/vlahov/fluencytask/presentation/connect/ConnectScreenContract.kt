package com.vlahov.fluencytask.presentation.connect

import android.bluetooth.BluetoothDevice
import androidx.compose.runtime.Immutable
import com.vlahov.fluencytask.base.UnidirectionalViewModel

interface ConnectScreenContract :
    UnidirectionalViewModel<ConnectScreenContract.State, ConnectScreenContract.Event, ConnectScreenContract.Effect> {

    @Immutable
    data class State(
        val hasBluetoothPermission: Boolean? = null,
        val bluetoothEnabled: Boolean = false,
        val devices: List<BluetoothDevice> = emptyList()
    )

    sealed interface Event {
        data object OnBackClicked : Event
        data class OnBluetoothEnableResult(val enabled: Boolean) : Event
        data class OnPermissionResult(val granted: Boolean) : Event
        data class OnDeviceClicked(val device: BluetoothDevice) : Event
    }

    sealed interface Effect {
        data object NavigateBack : Effect
    }
}