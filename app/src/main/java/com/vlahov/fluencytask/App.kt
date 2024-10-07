package com.vlahov.fluencytask

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
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
        }
    }
}