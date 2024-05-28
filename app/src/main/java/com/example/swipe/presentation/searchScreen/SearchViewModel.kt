package com.example.swipe.presentation.searchScreen

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.example.swipe.R
import com.example.swipe.datamodels.ProductListItem
import com.example.swipe.presentation.baseViewState.ScreenState
import com.example.swipe.presentation.coreBase.ComposeBaseViewModel
import com.example.swipe.usecase.ProductUseCase
import com.example.swipe.utils.ResourceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val productUseCase: ProductUseCase) :
    ComposeBaseViewModel<SearchScreenState, Nothing>() {
    fun fetchItems() {
        viewModelScope.launch {
            productUseCase.fetchProductList().collectLatest { resourceState ->
                when (resourceState) {
                    is ResourceState.Error -> {
                        setErrorState(message = R.string.something_went_wrong, icon = R.drawable.ic_empty_favourites)
                    }

                    is ResourceState.Loading -> {
                        setLoadingState()
                    }

                    is ResourceState.Success -> {
                        if (resourceState.data != null) {
                            setScreenState(
                                getCurrentState().copy(
                                    isLoading = false,
                                    error = null,
                                    data = SearchScreenState.SuccessState(dataList = resourceState.data)
                                )
                            )
                        } else {
                            setScreenState(
                                getCurrentState().copy(
                                    isLoading = false,
                                    error = null,
                                    data = SearchScreenState.EmptyState(
                                        icon = R.drawable.ic_empty_feed,
                                        textPrimary = R.string.nothing_found,
                                        textSecondary = R.string.text_search_holder_secondary,
                                        actionStringRes = 0,
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun searchItem(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val item = getCurrentState().data is SearchScreenState.SuccessState
            if (item) {
                val data = getCurrentState().data as SearchScreenState.SuccessState
                val filteredList = data.dataList?.filter {
                    it.product_name.contains(
                        query,
                        ignoreCase = true
                    )
                }
                if (filteredList.isNullOrEmpty()) {
                    setScreenState(
                        getCurrentState().copy(
                            error = null,
                            data = SearchScreenState.EmptyState(
                                icon = R.drawable.ic_empty_feed,
                                textPrimary = R.string.nothing_found,
                                textSecondary = R.string.text_search_holder_secondary,
                                actionStringRes = 0,
                            )
                        )
                    )
                } else {
                    setScreenState(
                        getCurrentState().copy(error = null,data = SearchScreenState.SuccessState(filteredList))
                    )
                }

            }
        }
    }

    override fun getInitialState(): ScreenState<SearchScreenState> {
        return ScreenState(isLoading = true, data = null)
    }

    override fun onCleared() {
        super.onCleared()
        setScreenState(getCurrentState().copy(isLoading = true, data = null))
    }

}

sealed class SearchScreenState {
    data class SuccessState(val dataList: List<ProductListItem>) : SearchScreenState()
    data class EmptyState(
        @DrawableRes val icon: Int,
        @StringRes val textPrimary: Int,
        @StringRes val textSecondary: Int,
        @StringRes val actionStringRes: Int,
    ) : SearchScreenState()
}

