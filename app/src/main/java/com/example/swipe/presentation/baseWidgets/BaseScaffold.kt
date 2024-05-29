package com.example.swipe.presentation.baseWidgets

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.swipe.ui.theme.LocalNetworkStatus
import com.example.swipe.utils.connectionStateHelper.ConnectionState

@Composable
fun BaseScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    content: @Composable (PaddingValues) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var isOfflineToOnline by remember { mutableStateOf(false) }

    val status = LocalNetworkStatus.current
    LaunchedEffect(key1 = status) {
        if (status is ConnectionState.Available) {
            if (isOfflineToOnline) {
                snackbarHostState.showSnackbar("You are online!")
            }
        }

        if (status is ConnectionState.Unavailable) {
            isOfflineToOnline = true
            snackbarHostState.showSnackbar("You are offline!")
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = topBar,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        content = content
    )
}