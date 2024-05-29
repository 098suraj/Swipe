package com.example.swipe.presentation.baseViewState

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
/**
 *  A data class that represents an error state for a screen in a composable UI.
 * @param message The resource ID of the string to be used for the error message.
 * @param icon The resource ID of the drawable to be used for the error icon.
 */
data class ScreenError(
    @StringRes val message: Int,
    @DrawableRes val icon: Int,
)