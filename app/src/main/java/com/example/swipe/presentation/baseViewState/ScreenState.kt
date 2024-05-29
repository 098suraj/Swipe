package com.example.swipe.presentation.baseViewState
/**
 * A data class that represents the state of a screen in a composable UI.
 *
 * @param T The type of the data being managed by the screen state.
 * @param isLoading A boolean indicating whether the screen is currently loading.
 * @param error An optional ScreenError object representing any error state of the screen.
 *              If null, it indicates no error.
 * @param data An optional data object of type T representing the content to be displayed on the screen.
 *             If null, it indicates that there is no data available or the data is still loading.
 */
data class ScreenState<T>(
    val isLoading: Boolean,
    val error: ScreenError? = null,
    val data: T? = null
)