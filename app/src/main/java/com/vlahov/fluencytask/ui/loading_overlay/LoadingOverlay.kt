package com.vlahov.fluencytask.ui.loading_overlay

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.vlahov.fluencytask.ui.theme.FluencyTaskTheme

/**
 * A simple dialog loading overlay, blocks user input while shown
 */
@Composable
fun LoadingOverlay() {
    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            CircularProgressIndicator()
        }
    }
}

@Preview
@Composable
private fun Preview() {
    FluencyTaskTheme {
        LoadingOverlay()
    }
}
