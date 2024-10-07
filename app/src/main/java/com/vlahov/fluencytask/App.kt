package com.vlahov.fluencytask

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.vlahov.fluencytask.base.collectInLaunchedEffect
import com.vlahov.fluencytask.presentation.chat.ChatScreen
import com.vlahov.fluencytask.presentation.chat.chatScreen
import com.vlahov.fluencytask.presentation.connect.ConnectScreen
import com.vlahov.fluencytask.presentation.connect.connectScreen
import com.vlahov.fluencytask.presentation.host.HostScreen
import com.vlahov.fluencytask.presentation.host.hostScreen
import com.vlahov.fluencytask.presentation.main.MainScreen
import com.vlahov.fluencytask.presentation.main.mainScreen
import com.vlahov.fluencytask.ui.theme.FluencyTaskTheme

@Composable
fun App() {
    val navController = rememberNavController()

    val viewModel = hiltViewModel<AppViewModel>()

    viewModel.effect.collectInLaunchedEffect { effect ->
        when(effect) {
            AppContract.Effect.NavigateToChatScreen -> navController.navigate(ChatScreen) {
                popUpTo(MainScreen)
            }

            AppContract.Effect.NavigateToMenu -> navController.popBackStack(route = MainScreen, inclusive = false)
        }
    }

    FluencyTaskTheme {
        NavHost(
            navController = navController,
            startDestination = MainScreen
        ) {
            mainScreen(
                onNavigateToConnect = { navController.navigate(ConnectScreen) },
                onNavigateToHost = { navController.navigate(HostScreen) }
            )

            connectScreen(
                onNavigateBack = navController::navigateUp
            )

            hostScreen(
                onNavigateBack = navController::navigateUp
            )

            chatScreen(
                onNavigateBack = navController::navigateUp
            )
        }
    }
}