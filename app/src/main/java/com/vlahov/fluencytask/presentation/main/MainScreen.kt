package com.vlahov.fluencytask.presentation.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.vlahov.fluencytask.R
import com.vlahov.fluencytask.base.collectInLaunchedEffect
import com.vlahov.fluencytask.base.screenViewModel
import com.vlahov.fluencytask.ui.layout.BaseScreen
import com.vlahov.fluencytask.ui.theme.FluencyTaskTheme
import com.vlahov.fluencytask.ui.theme.LocalScreenPadding
import com.vlahov.fluencytask.ui.theme.LocalSpacing
import kotlinx.serialization.Serializable

@Serializable
object MainScreen

fun NavGraphBuilder.mainScreen(
    onNavigateToConnect: () -> Unit,
    onNavigateToHost: () -> Unit,
) {
    composable<MainScreen> {
        val viewModel = screenViewModel<MainScreenViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()

        viewModel.effect.collectInLaunchedEffect { effect ->
            when (effect) {
                MainScreenContract.Effect.NavigateToConnect -> onNavigateToConnect()
                MainScreenContract.Effect.NavigateToHost -> onNavigateToHost()
            }
        }

        MainScreen(
            state = state,
            onEvent = viewModel::onEvent,
        )
    }
}

@Composable
private fun MainScreen(
    state: MainScreenContract.State,
    onEvent: (MainScreenContract.Event) -> Unit,
) {
    BaseScreen(
        modifier = Modifier.padding(LocalScreenPadding.current),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(LocalSpacing.current, Alignment.CenterVertically)
    ) {
        MenuButton(
            onClick = { onEvent(MainScreenContract.Event.OnConnectClicked) },
            text = stringResource(id = R.string.connect_to_chat),
            icon = Icons.Default.BluetoothConnected,
            modifier = Modifier.fillMaxWidth()
        )

        Text(text = stringResource(id = R.string.or))

        MenuButton(
            onClick = { onEvent(MainScreenContract.Event.OnHostClicked) },
            text = stringResource(id = R.string.host_chat),
            icon = Icons.Default.Bluetooth,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun MenuButton(
    onClick: () -> Unit,
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        contentPadding = PaddingValues(vertical = 16.dp),
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null
        )

        Spacer(modifier = Modifier.padding(end = LocalSpacing.current))

        Text(text = text, fontSize = 20.sp)
    }
}

@Preview
@Composable
private fun Preview() {
    FluencyTaskTheme {
        MainScreen(
            state = MainScreenContract.State(),
            onEvent = { }
        )
    }
}