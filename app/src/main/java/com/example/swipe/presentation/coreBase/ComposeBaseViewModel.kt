package com.example.swipe.presentation.coreBase

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swipe.presentation.baseViewState.ScreenError
import com.example.swipe.presentation.baseViewState.ScreenState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Abstract base class for ViewModels used in Jetpack Compose applications.
 *
 * This abstract class provides a foundation for ViewModels used in Jetpack Compose applications,
 * encapsulating common functionality related to managing UI state and actions.
 *
 * @param T The type of data held in the UI state.
 * @param U The type of UI action to be emitted.
 */

abstract class ComposeBaseViewModel<T, U> : ViewModel() {
    // Mutable state flow representing the UI state
    private val _uiState: MutableStateFlow<ScreenState<T>> by lazy {
        MutableStateFlow(ScreenState(isLoading = true, data = null))
    }
    // Mutable shared flow for emitting UI actions
    val uiState: StateFlow<ScreenState<T>> by lazy {
        _uiState.asStateFlow()
    }

    private val _uiAction: MutableSharedFlow<U> by lazy {
        MutableSharedFlow()
    }
    val uiAction: SharedFlow<U> by lazy {
        _uiAction.asSharedFlow()
    }

    /**
     * Sends a UI action to be processed by the ViewModel.
     *
     * @param action The UI action to be processed.
     */
    fun sendAction(action: U) = viewModelScope.launch(Dispatchers.Main) {
        _uiAction.emit(action)
    }

    /**
     * Sets the UI state to loading state.
     */
    fun setLoadingState() {
        _uiState.update {
            getCurrentState().copy(isLoading = true)
        }
    }

    /**
     * Hides the loading state from the UI.
     */
    fun hideLoadingState() {
        _uiState.update {
            getCurrentState().copy(isLoading = false)
        }
    }

    /**
     * Sets the UI state to an error state with the provided message and icon.
     *
     * @param message The resource ID of the error message.
     * @param icon The resource ID of the error icon.
     */
    fun setErrorState(@StringRes message: Int, @DrawableRes icon: Int) {
        _uiState.update {
            getCurrentState().copy(
                isLoading = false,
                error = ScreenError(message = message, icon = icon)
            )
        }
    }

    /**
     * Sets the UI state to the provided state.
     *
     * @param state The new UI state to be set.
     */
    fun setScreenState(state: ScreenState<T>) {
        _uiState.update { state }
    }

    /**
     * Retrieves the current UI state.
     *
     * @return The current UI state.
     */
    fun getCurrentState(): ScreenState<T> {
        return if (_uiState.value.data == null) getInitialState() else _uiState.value
    }

    /**
     * Retrieves the initial UI state.
     *
     * @return The initial UI state.
     */
    abstract fun getInitialState(): ScreenState<T>
}