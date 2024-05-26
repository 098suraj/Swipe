package com.example.swipe.datamodels

import androidx.paging.PagingData

data class ProductData(
    val viewType: Int,
    val productList : PagingData<ProductListItem>?
)
