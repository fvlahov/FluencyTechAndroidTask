package com.vlahov.fluencytask

import androidx.compose.runtime.Immutable
import com.vlahov.fluencytask.base.UnidirectionalViewModel

interface AppContract :
    UnidirectionalViewModel<AppContract.State, AppContract.Event, AppContract.Effect> {

    @Immutable
    data object State

    sealed interface Event

    sealed interface Effect {
        data object NavigateToChatScreen : Effect
        data object NavigateToMenu : Effect
    }
}