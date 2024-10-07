package com.vlahov.fluencytask.presentation.host

import android.annotation.SuppressLint
import com.vlahov.fluencytask.base.BaseViewModel
import com.vlahov.fluencytask.service.HostChatUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HostScreenViewModel @Inject constructor(
    private val hostChatUseCase: HostChatUseCase
) : BaseViewModel(), HostScreenContract {

    private val mutableState = MutableStateFlow(HostScreenContract.State())
    override val state: StateFlow<HostScreenContract.State> = mutableState.asStateFlow()

    private val mutableEffect = Channel<HostScreenContract.Effect>()
    override val effect = mutableEffect.receiveAsFlow()


    override fun onEvent(event: HostScreenContract.Event) {
        when (event) {
            HostScreenContract.Event.OnBackClicked -> mutableEffect.trySend(HostScreenContract.Effect.NavigateBack)
            HostScreenContract.Event.OnCancelHostingClicked -> mutableEffect.trySend(HostScreenContract.Effect.NavigateBack)
            is HostScreenContract.Event.OnPermissionResult -> {
                mutableState.update { it.copy(hasBluetoothPermission = event.granted) }
            }

            is HostScreenContract.Event.OnBluetoothEnableResult -> {
                if (event.enabled)
                    hostChat()

                mutableState.update { it.copy(bluetoothEnabled = event.enabled) }
            }
        }
    }

    // We're calling this function ONLY after granting bluetooth permission
    @SuppressLint("MissingPermission")
    private fun hostChat() {
        launchWithProgressIn(
            onStart = { mutableState.update { it.copy(isHosting = true) } },
            onFinish = { mutableState.update { it.copy(isHosting = false) } },
        ) {
            hostChatUseCase.awaitConnection()
        }
    }
}
