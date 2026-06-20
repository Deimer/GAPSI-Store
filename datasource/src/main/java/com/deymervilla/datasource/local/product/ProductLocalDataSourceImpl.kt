package com.deymervilla.datasource.local.product

import com.deymervilla.database.dao.ProductDao
import com.deymervilla.database.entities.ProductEntity
import javax.inject.Inject

class ProductLocalDataSourceImpl @Inject constructor(
    private val productDao: ProductDao
) : ProductLocalDataSource {

    override suspend fun fetchByKeyword(
        keyword: String
    ) = productDao.fetchByKeyword(
        keyword
    )

    override suspend fun fetchByKeywordAndPage(
        keyword: String,
        page: Int
    ) = productDao.fetchByKeywordAndPage(
        keyword, page
    )

    override suspend fun insert(
        products: List<ProductEntity>
    ): Boolean {
        val result = productDao.insert(products)
        return result.isNotEmpty() && result.all { it > 0 }
    }

    override suspend fun deleteByKeyword(
        keyword: String
    ) = productDao.deleteByKeyword(
        keyword
    ) >= 0

    override suspend fun deleteAll() =
        productDao.deleteAll() >= 0
}