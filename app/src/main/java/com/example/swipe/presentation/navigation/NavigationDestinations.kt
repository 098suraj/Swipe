package com.example.swipe.presentation.navigation

sealed class NavigationDestinations(val route:String) {
    object Home : NavigationDestinations(route = "Home")
    object SearchScreen : NavigationDestinations(route = "SearchScreen")
}