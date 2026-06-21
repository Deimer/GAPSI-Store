package com.deymervilla.domain.models

data class ProductModel(
    val id: String,
    val title: String,
    val price: Double,
    val wasPrice: Double?,
    val thumbnailUrl: String,
    val averageRating: Double,
    val numberOfReviews: Int,
    val sellerName: String,
    val canonicalUrl: String,
    val category: String,
    val description: String,
    val isOutOfStock: Boolean
)