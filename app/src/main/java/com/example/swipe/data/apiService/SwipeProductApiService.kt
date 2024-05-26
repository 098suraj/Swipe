package com.example.swipe.data.apiService

import com.example.swipe.datamodels.AddProductResponse
import com.example.swipe.datamodels.ProductList
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface SwipeProductApiService {

    /**
     * Retrieves a list of swipe products from the API.
     * @return ProductList: The list of swipe products.
     */
    @GET("api/public/get")
    suspend fun getSwipeProductList() : ProductList

    /**
     * Sends product details to the API for addition.
     * @param productName: The name of the product.
     * @param productType: The type of the product.
     * @param price: The price of the product.
     * @param tax: The tax rate applicable to the product.
     * @param files: The images of the product (optional).
     * @return AddProductResponse: The response containing the status of the product addition.
     */
    @Multipart
    @POST("api/public/add")
    suspend fun sendProductDetails(
        @Part("product_name") productName: RequestBody,
        @Part("product_type") productType: RequestBody,
        @Part("price") price: RequestBody,
        @Part("tax") tax: RequestBody,
        @Part files: MultipartBody.Part?,
    ) : AddProductResponse
}