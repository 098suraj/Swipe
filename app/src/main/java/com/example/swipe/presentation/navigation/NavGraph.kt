package com.example.swipe.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.swipe.presentation.homeScreen.HomeScreen
import com.example.swipe.presentation.searchScreen.SearchScreen
import com.example.swipe.presentation.searchScreen.SearchViewModel
import com.example.swipe.ui.theme.LocalNetworkStatus
import com.example.swipe.utils.connectionStateHelper.ProvideCurrentConnectivityStatus

@Composable
fun SwipeNavHost(navHostController: NavHostController, startDestinations: NavigationDestinations) {
    ProvideCurrentConnectivityStatus {
        NavHost(navController = navHostController, startDestination = startDestinations.route) {
            composable(NavigationDestinations.Home.route) {
                CompositionLocalProvider(
                    androidx.lifecycle.compose.LocalLifecycleOwner provides androidx.compose.ui.platform.LocalLifecycleOwner.current,
                ) {
                    HomeScreen(
                        modifier = Modifier.fillMaxSize(),
                        navHostController = navHostController
                    )
                }
            }

            composable(NavigationDestinations.SearchScreen.route) {
                CompositionLocalProvider(
                    androidx.lifecycle.compose.LocalLifecycleOwner provides androidx.compose.ui.platform.LocalLifecycleOwner.current,
                ) {
                    SearchScreen(
                        modifier = Modifier.fillMaxSize(),
                        navController = navHostController,
                    )
                }
            }
        }
    }
}