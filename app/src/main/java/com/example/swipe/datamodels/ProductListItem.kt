package com.example.swipe.datamodels

import androidx.compose.runtime.Immutable
import java.util.UUID
import java.util.concurrent.atomic.AtomicLong

@Immutable
data class ProductListItem(
    val image: String,
    val price: Double,
    val product_name: String,
    val product_type: String,
    val tax: Double,
) {
    val key: String
        get() = "${UUID.randomUUID()}$image$product_name$price$product_type$tax"
}