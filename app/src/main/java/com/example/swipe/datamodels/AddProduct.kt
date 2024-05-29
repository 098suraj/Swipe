package com.example.swipe.datamodels

import androidx.compose.runtime.Immutable
import java.io.File

@Immutable
data class AddProduct(
    val productName: String,
    val productType: String,
    val price: Double,
    val tax: Double,
    val files: File?
)