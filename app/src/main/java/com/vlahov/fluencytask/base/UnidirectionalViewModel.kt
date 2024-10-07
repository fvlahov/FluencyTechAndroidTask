package com.vlahov.fluencytask.base

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface UnidirectionalViewModel<STATE, EVENT, EFFECT> {
    val state: StateFlow<STATE>
    val effect: Flow<EFFECT>
    fun onEvent(event: EVENT)
}