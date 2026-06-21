package com.deymervilla.domain.repositories.product

import androidx.paging.PagingData
import com.deymervilla.domain.models.ProductModel
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    fun searchProductsByKeyword(
        keyword: String
    ): Flow<PagingData<ProductModel>>

    suspend fun fetchFirstResultThumbnail(
        keyword: String
    ): String?
}