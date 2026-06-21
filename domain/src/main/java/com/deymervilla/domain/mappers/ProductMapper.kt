package com.deymervilla.domain.mappers

import com.deymervilla.database.entities.ProductEntity
import com.deymervilla.domain.models.ProductModel
import com.deymervilla.domain.utils.orZero
import com.deymervilla.network.dto.ProductDTO

fun ProductDTO.toEntity(searchKeyword: String, page: Int): ProductEntity = ProductEntity(
    id = productId.orEmpty(),
    title = title.orEmpty(),
    price = price.orZero(),
    wasPrice = wasPrice,
    thumbnailUrl = thumbnailUrl.orEmpty(),
    averageRating = averageRating.orZero(),
    numberOfReviews = numberOfReviews.orZero(),
    sellerName = sellerName.orEmpty(),
    canonicalUrl = canonicalUrl.orEmpty(),
    category = category.orEmpty(),
    description = description.orEmpty(),
    isOutOfStock = isOutOfStock,
    searchKeyword = searchKeyword,
    page = page
)

fun ProductEntity.toModel(): ProductModel = ProductModel(
    id = id,
    title = title,
    price = price,
    wasPrice = wasPrice,
    thumbnailUrl = thumbnailUrl,
    averageRating = averageRating,
    numberOfReviews = numberOfReviews,
    sellerName = sellerName,
    canonicalUrl = canonicalUrl,
    category = category,
    description = description,
    isOutOfStock = isOutOfStock
)