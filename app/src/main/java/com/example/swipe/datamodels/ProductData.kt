package com.example.swipe.datamodels

import androidx.paging.PagingData

data class ProductData(
    val productList : PagingData<ProductListItem>?
)
