package com.example.swipe.presentation.homeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.swipe.datamodels.ProductListItem
import com.example.swipe.usecase.ProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(useCase: ProductUseCase) : ViewModel() {
    private val pagingSource = useCase.fetchPaginatedProductListPagingSource()
    private val pagingData = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = { pagingSource }
    ).flow.cachedIn(viewModelScope)

    fun getPaginatedProducts(): Flow<PagingData<ProductListItem>> {
        return pagingData
    }

    fun invalidateData(){
        pagingSource.invalidate()
    }
}

