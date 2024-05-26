package com.example.swipe.datamodels

data class AddProductResponse(
    val message: String,
    val product_details: ProductListItem,
    val product_id: Int,
    val success: Boolean
)