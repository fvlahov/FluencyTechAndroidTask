package com.vlahov.fluencytask.presentation.main

import androidx.compose.runtime.Immutable
import com.vlahov.fluencytask.base.UnidirectionalViewModel

interface MainScreenContract :
    UnidirectionalViewModel<MainScreenContract.State, MainScreenContract.Event, MainScreenContract.Effect> {

    @Immutable
    data class State(
        val hasBluetoothPermission: Boolean? = null,
        val bluetoothEnabled: Boolean = false
    )

    sealed interface Event {
        data object OnConnectClicked : Event
        data object OnHostClicked : Event
    }

    sealed interface Effect {
        data object NavigateToConnect : Effect
        data object NavigateToHost : Effect
    }

}