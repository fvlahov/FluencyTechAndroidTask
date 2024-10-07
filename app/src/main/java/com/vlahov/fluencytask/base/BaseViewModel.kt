package com.vlahov.fluencytask.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    private val mutableLoadingFlow = MutableStateFlow(false)
    val loadingFlow = mutableLoadingFlow.asStateFlow()

    protected fun launchIn(
        coroutineScope: CoroutineScope = viewModelScope,
        action: suspend CoroutineScope.() -> Unit,
    ): Job =
        coroutineScope.launch {
            action()
        }


    protected fun launchWithProgressIn(
        coroutineScope: CoroutineScope = viewModelScope,
        onStart: () -> Unit = { mutableLoadingFlow.update { true } },
        onFinish: () -> Unit = { mutableLoadingFlow.update { false } },
        action: suspend CoroutineScope.() -> Unit,
    ) = launchIn(
        coroutineScope = coroutineScope
    ) {
        onStart()

        action()

        onFinish()
    }
}
