package com.vlahov.fluencytask.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vlahov.fluencytask.ui.loading_overlay.LoadingOverlay

/**
 * Provide viewmodel via [viewModelProvider]
 * Handles blocking loading via [BaseViewModel.loadingFlow]
 *
 * @see BaseViewModel.launchWithProgressIn
 */
@Composable
inline fun <reified T : BaseViewModel> screenViewModel(viewModelProvider: @Composable () -> T = { hiltViewModel<T>() }): T {
    val viewModel = viewModelProvider()

    val isLoading by viewModel.loadingFlow.collectAsStateWithLifecycle()

    if (isLoading)
        LoadingOverlay()

    return viewModel
}