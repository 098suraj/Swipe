package com.example.swipe.presentation.baseViewState

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class ScreenError(
    @StringRes val message: Int,
    @DrawableRes val icon: Int,
)