package com.example.swipe.datamodels

import androidx.compose.runtime.Immutable

@Immutable
data class ProductListItem(
    val image: String,
    val price: Double,
    val product_name: String,
    val product_type: String,
    val tax: Double
)