package com.example.swipe.presentation.searchScreen

import androidx.lifecycle.viewModelScope
import com.example.swipe.datamodels.ProductListItem
import com.example.swipe.presentation.baseViewState.ScreenError
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

    fun updateSearchQuery(query: String) {
        val currentScreenData = getCurrentState().data
        setScreenState(
            getCurrentState().copy(data = currentScreenData?.copy(searchText = query))
        )
        searchItem(query)
    }

    init {
        fetchItems()
    }

    private fun fetchItems() {
        viewModelScope.launch(Dispatchers.IO) {
            productUseCase.fetchProductList().collectLatest { data ->
                when (data) {
                    is ResourceState.Error -> {
                        setScreenState(
                            getCurrentState().copy(
                                isLoading = false,
                                error = ScreenError("Something went Wrong")
                            )
                        )
                    }

                    is ResourceState.Loading -> {
                        setScreenState(getCurrentState().copy(isLoading = true, error = null))
                    }

                    is ResourceState.Success -> {
                        val currentData = getCurrentState().data
                        setScreenState(
                            getCurrentState().copy(
                                isLoading = false,
                                error = null,
                                data = currentData?.copy(dataList = data.data)
                            )
                        )
                    }
                }
            }
        }
    }

     fun searchItem(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val filteredList = getCurrentState().data?.dataList?.filter {
                it.product_name.contains(
                    query,
                    ignoreCase = true
                )
            }
            val currentScreenData = getCurrentState().data
            setScreenState(
                getCurrentState().copy(data = currentScreenData?.copy(dataList = filteredList))
            )
        }
    }

    override fun getInitialState(): ScreenState<SearchScreenState> {
        return ScreenState(isLoading = true)
    }

}

data class SearchScreenState(
    val searchText: String? = "",
    val dataList: List<ProductListItem>?
)