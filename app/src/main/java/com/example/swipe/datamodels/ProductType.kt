package com.example.swipe.datamodels

enum class ProductType {
    VEHICLE, BOOK, CLOTHES, BOTTLE, ELECTRONICS, FOOD, BEVERAGES
}

val productTypeList = listOf(
    ProductType.VEHICLE,
    ProductType.BOOK,
    ProductType.CLOTHES,
    ProductType.BOTTLE,
    ProductType.ELECTRONICS,
    ProductType.FOOD, ProductType.BEVERAGES
).map { it.name.lowercase().replaceFirstChar(Char::titlecase) }