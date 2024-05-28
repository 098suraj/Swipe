package com.example.swipe.utils.connectionStateHelper

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.swipe.ui.theme.LocalNetworkStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Network utility to get current state of internet connection
 */
val Context.connectivityStatus: ConnectionState
    get() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        return getCurrentConnectivityState(connectivityManager)
    }


/**
 * Reusable network utility function to get current state of internet connection based on connectivity manager
 */
private fun getCurrentConnectivityState(connectivityManager: ConnectivityManager?): ConnectionState {
    val state = connectivityManager?.allNetworks?.any { connectivityManager.getNetworkCapabilities(it)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false } ?: false
    return if (state) ConnectionState.Available else ConnectionState.Unavailable
}

// Network connectivity status flow for views
fun Context.observeCurrentConnectivityStatus() = callbackFlow<ConnectionState> {

    // connectivity manager for network utilities
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

    // Call back function which gives has object of NetworkCallback and returns a type of ConnectionState -> Available or Unavailable
    val callback = NetworkCallback { connectionState -> trySend(connectionState) }

    // network request builder for getting the current internet capabilities
    val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    // registering the callback for network changes.
    try {
        // using dispatchers to avoid binder call on main thread
        withContext(Dispatchers.IO) {
            connectivityManager?.registerNetworkCallback(networkRequest, callback)
        }
    } catch (e: RuntimeException) {
        // Requesting a network with this method will count toward this limit.
        // If this limit is exceeded, an exception will be thrown.
        // To avoid hitting this issue and to conserve resources,
        // make sure to unregister the callbacks with unregisterNetworkCallback(ConnectivityManager.NetworkCallback).
        withContext(Dispatchers.IO) {
            connectivityManager?.unregisterNetworkCallback(callback)
        }
    }

    // Set current state
    val currentState = getCurrentConnectivityState(connectivityManager)
    trySend(currentState)

    // this method is used to dispose the callback flow
    awaitClose {
        //unregister the callbacks with unregisterNetworkCallback(ConnectivityManager.NetworkCallback).
        connectivityManager?.unregisterNetworkCallback(callback)
    }
} .distinctUntilChanged()
    .flowOn(Dispatchers.IO)


// compose based connectivity observer // produce state can be used as alternative of this composable
@Composable
fun ProvideCurrentConnectivityStatus(
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var currentConnectionState by remember { mutableStateOf<ConnectionState>(context.connectivityStatus) }

    DisposableEffect(key1 = context.connectivityStatus) {
        // connectivity manager for network utilities
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        // Call back function which gives has object of NetworkCallback and returns a type of ConnectionState -> Available or Unavailable
        val callback = NetworkCallback { connectionState -> currentConnectionState = connectionState }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        // registering the callback for network changes.
        try {
            // using dispatchers to avoid binder call on main thread
            coroutineScope.launch(Dispatchers.IO) {
                connectivityManager?.registerNetworkCallback(
                    networkRequest,
                    callback
                )
            }
        } catch (e: RuntimeException) {
            // Requesting a network with this method will count toward this limit.
            // If this limit is exceeded, an exception will be thrown.
            // To avoid hitting this issue and to conserve resources,
            // make sure to unregister the callbacks with unregisterNetworkCallback(ConnectivityManager.NetworkCallback).
            coroutineScope.launch(Dispatchers.IO) {
                connectivityManager?.unregisterNetworkCallback(
                    callback
                )
            }
        }

        // Set current state
        val currentState = getCurrentConnectivityState(connectivityManager)
        currentConnectionState = currentState

        onDispose {
            // using dispatchers to avoid binder call on main thread
            coroutineScope.launch(Dispatchers.IO) {
                connectivityManager?.unregisterNetworkCallback(
                    callback
                )
            }
        }
    }

    CompositionLocalProvider(
        value = LocalNetworkStatus provides currentConnectionState,
        content = content,
    )
}

// Network CallBack which gives call network state call backs
fun NetworkCallback(callback: (ConnectionState) -> Unit): ConnectivityManager.NetworkCallback {
    return object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            callback(ConnectionState.Available)
        }

        override fun onUnavailable() {
            super.onUnavailable()
            callback(ConnectionState.Unavailable)
        }


        override fun onLost(network: Network) {
            callback(ConnectionState.Unavailable)
        }
    }
}

