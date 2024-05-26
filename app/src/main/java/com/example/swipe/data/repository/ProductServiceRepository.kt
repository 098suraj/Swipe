package com.example.swipe.data.repository

import androidx.paging.PagingSource
import com.example.swipe.datamodels.AddProduct
import com.example.swipe.datamodels.AddProductResponse
import com.example.swipe.datamodels.ProductList
import com.example.swipe.datamodels.ProductListItem

interface ProductServiceRepository {
    /**
     * Retrieves a paginated list of swipe products.
     * @return PagingSource<Int, ProductListItem>: The paginated list of swipe products.
     */
    suspend fun getSwipeProductList(): PagingSource<Int, ProductListItem>

    /**
     * Retrieves a list of products.
     * @return ProductList: The list of products.
     */
    suspend fun getProductList(): ProductList

    /**
     * Sends product details to be added.
     * @param product: The product to be added.
     * @return AddProductResponse: The response containing the status of the product addition.
     */
    suspend fun sendProductDetails(product: AddProduct): AddProductResponse
}