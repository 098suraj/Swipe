package com.example.swipe.datamodels

import androidx.compose.runtime.Immutable
import java.util.concurrent.atomic.AtomicLong

@Immutable
data class ProductListItem(
    val image: String,
    val price: Double,
    val product_name: String,
    val product_type: String,
    val tax: Double,
    val key: AtomicLong = AtomicLong()
)