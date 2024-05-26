package com.example.swipe.utils.connectionStateHelper


/**
 * Connection States
 */
sealed class ConnectionState {
    data object Available : ConnectionState()
    data object Unavailable : ConnectionState()
}