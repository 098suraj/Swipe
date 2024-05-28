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

abstract class ComposeBaseViewModel<T, U> : ViewModel() {

    private val _uiState: MutableStateFlow<ScreenState<T>> by lazy {
        MutableStateFlow(ScreenState(isLoading = true, data = null))
    }
    val uiState: StateFlow<ScreenState<T>> by lazy {
        _uiState.asStateFlow()
    }

    private val _uiAction: MutableSharedFlow<U> by lazy {
        MutableSharedFlow()
    }
    val uiAction: SharedFlow<U> by lazy {
        _uiAction.asSharedFlow()
    }

    fun sendAction(action: U) = viewModelScope.launch(Dispatchers.Main) {
        _uiAction.emit(action)
    }

    fun setLoadingState() {
        _uiState.update {
            getCurrentState().copy(isLoading = true)
        }
    }

    fun hideLoadingState() {
        _uiState.update {
            getCurrentState().copy(isLoading = false)
        }
    }

    fun setErrorState(@StringRes message: Int,@DrawableRes icon:Int) {
        _uiState.update {
            getCurrentState().copy(
                isLoading = false,
                error = ScreenError(message = message, icon = icon)
            )
        }
    }

    fun setScreenState(state: ScreenState<T>) {
        _uiState.update { state }
    }

    fun getCurrentState(): ScreenState<T> {
        return if (_uiState.value.data == null) getInitialState() else _uiState.value
    }

    abstract fun getInitialState(): ScreenState<T>
}