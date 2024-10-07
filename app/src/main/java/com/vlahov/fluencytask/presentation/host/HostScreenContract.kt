package com.vlahov.fluencytask.presentation.host

import androidx.compose.runtime.Immutable
import com.vlahov.fluencytask.base.UnidirectionalViewModel

interface HostScreenContract :
    UnidirectionalViewModel<HostScreenContract.State, HostScreenContract.Event, HostScreenContract.Effect> {

    @Immutable
    data class State(
        val isHosting: Boolean = false,
        val hasBluetoothPermission: Boolean? = null,
        val bluetoothEnabled: Boolean = false
    )

    sealed interface Event {
        data object OnBackClicked : Event
        data object OnCancelHostingClicked : Event
        data class OnPermissionResult(val granted: Boolean) : Event
        data class OnBluetoothEnableResult(val enabled: Boolean) : Event
    }

    sealed interface Effect {
        data object NavigateBack : Effect
    }
}