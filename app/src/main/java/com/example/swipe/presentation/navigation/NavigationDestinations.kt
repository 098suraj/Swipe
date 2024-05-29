package com.example.swipe.presentation.navigation

/**
 * Sealed class representing the destinations in the navigation graph.
 *
 * This sealed class defines the different destinations available in the navigation graph.
 * Each destination is represented by an object extending this sealed class.
 *
 * @property route The route associated with the destination.
 */
sealed class NavigationDestinations(val route: String) {
    object Home : NavigationDestinations(route = "Home")
    object SearchScreen : NavigationDestinations(route = "SearchScreen")
}