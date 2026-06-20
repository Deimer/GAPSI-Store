package com.deymervilla.datasource.local.product

import com.deymervilla.database.entities.ProductEntity

interface ProductLocalDataSource {

    suspend fun fetchByKeyword(
        keyword: String
    ): List<ProductEntity>

    suspend fun fetchByKeywordAndPage(
        keyword: String,
        page: Int
    ): List<ProductEntity>

    suspend fun insert(
        products: List<ProductEntity>
    ): Boolean

    suspend fun deleteByKeyword(
        keyword: String
    ): Boolean

    suspend fun deleteAll(): Boolean
}