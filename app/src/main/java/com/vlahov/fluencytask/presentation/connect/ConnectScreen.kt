@file:OptIn(ExperimentalPermissionsApi::class)

package com.vlahov.fluencytask.presentation.connect

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.vlahov.fluencytask.ui.layout.BaseScreenWithTopBar
import com.vlahov.fluencytask.ui.theme.FluencyTaskTheme
import com.vlahov.fluencytask.ui.theme.LocalScreenPadding
import com.vlahov.fluencytask.ui.theme.LocalSpacing
import kotlinx.serialization.Serializable

@Serializable
object ConnectScreen

fun NavGraphBuilder.connectScreen(
    onNavigateBack: () -> Unit,
) {
    composable<ConnectScreen> {
        val viewModel = screenViewModel<ConnectScreenViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()

        viewModel.effect.collectInLaunchedEffect { effect ->
            when (effect) {
                ConnectScreenContract.Effect.NavigateBack -> onNavigateBack()
            }
        }


        val enableBluetoothActivityResult = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            viewModel.onEvent(
                ConnectScreenContract.Event.OnBluetoothEnableResult(
                    enabled = result.resultCode == Activity.RESULT_OK
                )
            )
        }

        val bluetoothPermission = rememberMultiplePermissionsState(
            permissions = bluetoothConnectPermissions,
            onPermissionsResult = { result ->
                viewModel.onEvent(ConnectScreenContract.Event.OnPermissionResult(granted = result.all { true }))
                enableBluetoothActivityResult.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            }
        )

        LaunchedEffect(Unit) {
            bluetoothPermission.launchMultiplePermissionRequest()
        }

        ConnectScreen(
            state = state,
            onEvent = viewModel::onEvent,
        )
    }
}

@Composable
private fun ConnectScreen(
    state: ConnectScreenContract.State,
    onEvent: (ConnectScreenContract.Event) -> Unit,
) {
    BaseScreenWithTopBar(
        title = stringResource(id = R.string.connect_to_device),
        onBackClicked = { onEvent(ConnectScreenContract.Event.OnBackClicked) },
        modifier = Modifier.padding(LocalScreenPadding.current),
        verticalArrangement = Arrangement.spacedBy(LocalSpacing.current)
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(LocalSpacing.current)
        ) {
            items(state.devices, key = { it.address }) { device ->
                Card(
                    onClick = { onEvent(ConnectScreenContract.Event.OnDeviceClicked(device)) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = device.name.orEmpty(),
                        modifier = Modifier.padding(LocalSpacing.current)
                    )
                }
            }
        }
    }
}

private val bluetoothConnectPermissions = if(Build.VERSION.SDK_INT <= 30)
    listOf(
        android.Manifest.permission.BLUETOOTH,
    )
else
    listOf(
        android.Manifest.permission.BLUETOOTH_CONNECT,
        android.Manifest.permission.BLUETOOTH_SCAN,
    )

@Preview
@Composable
private fun Preview() {
    FluencyTaskTheme {
        ConnectScreen(
            state = ConnectScreenContract.State(),
            onEvent = { }
        )
    }
}