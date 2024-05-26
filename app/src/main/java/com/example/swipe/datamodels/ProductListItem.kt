package com.example.swipe.datamodels

data class ProductListItem(
    val image: String,
    val price: Double,
    val product_name: String,
    val product_type: String,
    val tax: Double
)