package com.example.swipe.presentation.homeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.InvalidatingPagingSourceFactory
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.swipe.datamodels.ProductListItem
import com.example.swipe.usecase.ProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * ViewModel class responsible for managing data and business logic related to the home screen.
 *
 * This ViewModel is responsible for fetching and managing paginated product data
 * to be displayed on the home screen. It interacts with the [ProductUseCase] to fetch
 * the paginated product list paging source and exposes the data as a flow of [PagingData].
 *
 * @param useCase The use case for accessing product-related data and business logic.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(useCase: ProductUseCase) : ViewModel() {
    // Paging source for fetching paginated product list data

    private val invalidatingPagingSourceFactory = InvalidatingPagingSourceFactory {
        useCase.fetchPaginatedProductListPagingSource()
    }

    // Flow representing the paginated product data
    private val pagingData = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = invalidatingPagingSourceFactory
    ).flow.cachedIn(viewModelScope)

    /**
     * Retrieves the flow of paginated product data.
     *
     * @return A flow of [PagingData] representing the paginated product data.
     */
    fun getPaginatedProducts(): Flow<PagingData<ProductListItem>> {
        return pagingData
    }

}

