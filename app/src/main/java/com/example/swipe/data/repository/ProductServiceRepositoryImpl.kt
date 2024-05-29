package com.example.swipe.data.repository

import androidx.paging.PagingSource
import com.example.swipe.data.apiService.SwipeProductApiService
import com.example.swipe.datamodels.AddProduct
import com.example.swipe.datamodels.AddProductResponse
import com.example.swipe.datamodels.ProductList
import com.example.swipe.datamodels.ProductListItem
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

/**
 * Implementation class for ProductServiceRepository interface
 * This layer can easily be swapped out with other implementation and helps in testing as well.
 */

class ProductServiceRepositoryImpl @Inject constructor(private val swipeProductApiService: SwipeProductApiService) : ProductServiceRepository {
    override fun getSwipeProductListPagingSource(): PagingSource<Int, ProductListItem> {
        return SwipePagingSource(swipeProductApiService = swipeProductApiService)
    }

    override suspend fun getProductList(): ProductList {
      return swipeProductApiService.getSwipeProductList()
    }

    override suspend fun sendProductDetails(product: AddProduct): AddProductResponse {
      return swipeProductApiService.sendProductDetails(
          productName = product.productName.toRequestBody(),
          productType = product.productType.toRequestBody(),
          price = product.price.toString().toRequestBody(),
          tax = product.tax.toString().toRequestBody(),
          files = product.files?.asRequestBody("image/*".toMediaTypeOrNull())?.let {
              MultipartBody.Part.createFormData(
                  "files[]",
                  product.files?.name,
                  it
              )
          }
      )
    }
}