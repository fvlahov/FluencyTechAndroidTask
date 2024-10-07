@file:OptIn(ExperimentalPermissionsApi::class)

package com.vlahov.fluencytask.presentation.host

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.vlahov.fluencytask.R
import com.vlahov.fluencytask.base.collectInLaunchedEffect
import com.vlahov.fluencytask.base.screenViewModel
import com.vlahov.fluencytask.ui.layout.BaseScreen
import com.vlahov.fluencytask.ui.layout.BaseScreenWithTopBar
import com.vlahov.fluencytask.ui.theme.FluencyTaskTheme
import com.vlahov.fluencytask.ui.theme.LocalScreenPadding
import com.vlahov.fluencytask.ui.theme.LocalSpacing
import kotlinx.serialization.Serializable

@Serializable
object HostScreen

fun NavGraphBuilder.hostScreen(
    onNavigateBack: () -> Unit,
) {
    composable<HostScreen> {
        val viewModel = screenViewModel<HostScreenViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()

        viewModel.effect.collectInLaunchedEffect { effect ->
            when (effect) {
                HostScreenContract.Effect.NavigateBack -> onNavigateBack()
            }
        }


        val enableBluetoothActivityResult = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            viewModel.onEvent(
                HostScreenContract.Event.OnBluetoothEnableResult(
                    enabled = result.resultCode == Activity.RESULT_OK
                )
            )
        }

        val enableDiscoverableActivityResult = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) {
            enableBluetoothActivityResult.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        }

        val bluetoothPermission = rememberMultiplePermissionsState(
            permissions = bluetoothHostPermissions,
            onPermissionsResult = { result ->
                viewModel.onEvent(HostScreenContract.Event.OnPermissionResult(granted = result.all { true }))
                enableDiscoverableActivityResult.launch(Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE))
            }
        )

        LaunchedEffect(Unit) {
            bluetoothPermission.launchMultiplePermissionRequest()
        }

        HostScreen(
            state = state,
            onEvent = viewModel::onEvent,
        )
    }
}

@Composable
private fun HostScreen(
    state: HostScreenContract.State,
    onEvent: (HostScreenContract.Event) -> Unit,
) {
    BaseScreenWithTopBar(
        title = stringResource(id = R.string.host_chat),
        onBackClicked = { onEvent(HostScreenContract.Event.OnBackClicked) },
        modifier = Modifier.padding(
            horizontal = LocalScreenPadding.current
        ),
    ) {
        AnimatedContent(
            targetState = state.hasBluetoothPermission,
            label = "Permission content animation"
        ) { hasPermission ->
            when (hasPermission) {
                true -> BaseScreen(
                    verticalArrangement = Arrangement.spacedBy(LocalSpacing.current),
                ) {
                    AnimatedVisibility(
                        visible = state.isHosting,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(text = stringResource(id = R.string.awaiting_connection))
                    }

                    Button(
                        onClick = { onEvent(HostScreenContract.Event.OnCancelHostingClicked) },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                }

                false -> BaseScreen(
                    verticalArrangement = Arrangement.spacedBy(LocalSpacing.current),
                ) {
                    Text(
                        text = stringResource(id = R.string.bluetooth_permission_denied)
                    )
                }

                null -> BaseScreen(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }
        }


    }
}

@Preview
@Composable
private fun PreviewPermissionGranted() {
    FluencyTaskTheme {
        HostScreen(
            state = HostScreenContract.State(
                hasBluetoothPermission = true,
                isHosting = true
            ),
            onEvent = { }
        )
    }
}

private val bluetoothHostPermissions = if(Build.VERSION.SDK_INT <= 30)
    listOf(
        android.Manifest.permission.BLUETOOTH,
    )
else
    listOf(android.Manifest.permission.BLUETOOTH_CONNECT)

@Preview
@Composable
private fun PreviewPermissionDenied() {
    FluencyTaskTheme {
        HostScreen(
            state = HostScreenContract.State(
                hasBluetoothPermission = false,
            ),
            onEvent = { }
        )
    }
}

@Preview
@Composable
private fun PreviewAwaitingPermission() {
    FluencyTaskTheme {
        HostScreen(
            state = HostScreenContract.State(
                hasBluetoothPermission = null,
            ),
            onEvent = { }
        )
    }
}