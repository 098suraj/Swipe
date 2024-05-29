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

/**
 * ViewModel class responsible for managing data and business logic related to the search screen.
 *
 * This ViewModel is responsible for fetching and managing product search results,
 * handling loading states, errors, and search queries.
 *
 * @param productUseCase The use case for accessing product-related data and business logic.
 */
@HiltViewModel
class SearchViewModel @Inject constructor(private val productUseCase: ProductUseCase) :
    ComposeBaseViewModel<SearchScreenState, Nothing>() {
    // Mutable list to store product items
    private var productList = mutableListOf<ProductListItem>()

    /**
     * Fetches the initial list of product items.
     * Sets the [SearchScreenState] depending on api response.
     */
    fun fetchItems() {
        viewModelScope.launch {
            productUseCase.fetchProductList().collectLatest { resourceState ->
                when (resourceState) {
                    is ResourceState.Error ->
                        setErrorState(
                            message = R.string.something_went_wrong,
                            icon = R.drawable.ic_empty_favourites
                        )

                    is ResourceState.Loading -> setLoadingState()
                    is ResourceState.Success -> handleSuccessState(resourceState.data)
                }
            }
        }
    }

    /**
     * Handles the success state of fetching product items.
     *
     * This function constructs the appropriate state based on the fetched data.
     * If the data is not null, it creates a [SearchScreenState.SuccessState] with the provided list of product items.
     * If the data is null, it creates a [SearchScreenState.EmptyState] indicating no items found.
     *
     * @param data The list of product items fetched from the data source.
     */
    private fun handleSuccessState(data: List<ProductListItem>?) {
        val newState = if (data != null) {
            SearchScreenState.SuccessState(data)
        } else {
            SearchScreenState.EmptyState(
                icon = R.drawable.ic_empty_feed,
                textPrimary = R.string.nothing_found,
                textSecondary = R.string.text_search_holder_secondary,
                actionStringRes = 0
            )
        }
        setScreenState(getCurrentState().copy(isLoading = false, error = null, data = newState))
    }

    /**
     * Searches for items based on the provided query.
     * Sets [SearchScreenState] depending on item available
     * @param query The search query.
     */
    fun searchItem(query: String) {
        if (query.isEmpty()) {
            // Display all items if query is empty
            setScreenState(
                getCurrentState().copy(
                    error = null,
                    data = SearchScreenState.SuccessState(productList)
                )
            )
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val item = getCurrentState().data is SearchScreenState.SuccessState
            if (item) {
                val data = getCurrentState().data as SearchScreenState.SuccessState
                val filteredList = data.dataList.filter {
                    it.product_name.contains(
                        query,
                        ignoreCase = true
                    )
                }
                val newState = if (filteredList.isNullOrEmpty()) {
                    // Set empty state if no items match the search query
                    SearchScreenState.EmptyState(
                        icon = R.drawable.ic_empty_feed,
                        textPrimary = R.string.nothing_found,
                        textSecondary = R.string.text_search_holder_secondary,
                        actionStringRes = 0
                    )
                } else {
                    // Update UI state with filtered items if matches found
                    SearchScreenState.SuccessState(filteredList)
                }
                setScreenState(getCurrentState().copy(error = null, data = newState))
            }
        }
    }

    /**
     * Retrieves the initial UI state.
     *
     * @return The initial UI state.
     */
    override fun getInitialState(): ScreenState<SearchScreenState> {
        return ScreenState(isLoading = true, data = null)
    }

    /**
     * Cleans up resources when the ViewModel is cleared.
     */
    override fun onCleared() {
        super.onCleared()
        // Reset UI state
        setScreenState(getCurrentState().copy(isLoading = true, data = null))
    }

}

/**
 * Sealed class representing the different states of the search screen.
 *
 * This sealed class defines the various states that the search screen can be in.
 * Each state is represented by a subclass of this sealed class.
 */

sealed class SearchScreenState {
    /**
     * Data class representing the success state of the search screen.
     *
     * @param dataList The list of product items representing the search results.
     */
    data class SuccessState(val dataList: List<ProductListItem>) : SearchScreenState()

    /**
     * Data class representing the empty state of the search screen.
     *
     * @param icon The resource ID of the icon to be displayed.
     * @param textPrimary The resource ID of the primary text to be displayed.
     * @param textSecondary The resource ID of the secondary text to be displayed.
     * @param actionStringRes The resource ID of the action string to be displayed.
     */
    data class EmptyState(
        @DrawableRes val icon: Int,
        @StringRes val textPrimary: Int,
        @StringRes val textSecondary: Int,
        @StringRes val actionStringRes: Int,
    ) : SearchScreenState()
}

