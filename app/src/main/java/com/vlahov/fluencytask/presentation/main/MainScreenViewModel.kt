package com.vlahov.fluencytask.presentation.main

import com.vlahov.fluencytask.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
) : BaseViewModel(), MainScreenContract {

    private val mutableState = MutableStateFlow(MainScreenContract.State())
    override val state: StateFlow<MainScreenContract.State> = mutableState.asStateFlow()

    private val mutableEffect = Channel<MainScreenContract.Effect>()
    override val effect = mutableEffect.receiveAsFlow()


    override fun onEvent(event: MainScreenContract.Event) {
        when (event) {
            MainScreenContract.Event.OnConnectClicked -> mutableEffect.trySend(MainScreenContract.Effect.NavigateToConnect)
            MainScreenContract.Event.OnHostClicked -> mutableEffect.trySend(MainScreenContract.Effect.NavigateToHost)
        }
    }
}
