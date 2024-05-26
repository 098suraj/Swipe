package com.example.swipe.presentation.baseViewState

data class ScreenState<T>(
    val isLoading: Boolean,
    val error: ScreenError? = null,
    val data: T? = null
)