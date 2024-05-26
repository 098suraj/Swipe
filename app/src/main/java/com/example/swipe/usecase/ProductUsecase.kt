package com.example.swipe.usecase

import androidx.paging.PagingSource
import com.example.swipe.data.repository.ProductServiceRepository
import com.example.swipe.datamodels.AddProduct
import com.example.swipe.datamodels.AddProductResponse
import com.example.swipe.datamodels.ProductList
import com.example.swipe.datamodels.ProductListItem
import com.example.swipe.utils.ResourceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ProductUseCase @Inject constructor(private val repository: ProductServiceRepository) {

    /**
     * Fetches a paginated list of products.
     */
    suspend fun fetchPaginatedProductList(): Flow<ResourceState<PagingSource<Int, ProductListItem>>> = flow {
        try {
            emit(ResourceState.Loading())
            val response = repository.getSwipeProductList()
            emit(ResourceState.Success(response))
        } catch (e: Exception){
            e.printStackTrace()
            emit(ResourceState.Error(message = e.localizedMessage ?: "An unexpected error occurred in fetching products list"))
        }
    }

    /**
     * Fetches a list of products.
     */
    suspend fun fetchProductList(): Flow<ResourceState<ProductList>> = flow {
        try {
            emit(ResourceState.Loading())
            val response = repository.getProductList()
            emit(ResourceState.Success(response))
        } catch (e: Exception){
            e.printStackTrace()
            emit(ResourceState.Error(message = e.localizedMessage ?: "An unexpected error occurred in fetching products list"))
        }
    }

    /**
     * Adds product data to the repository.
     * @param productListItem: The product to be added.
     * @return AddProductResponse?: The response containing the status of the product addition,
     *                              or null if an error occurred.
     */
    suspend fun addProductData(productListItem: AddProduct): AddProductResponse? {
        return try {
            repository.sendProductDetails(productListItem)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
