package com.deymervilla.datasource.remote.product

import com.deymervilla.network.dto.ProductDTO

interface ProductRemoteDataSource {

    suspend fun searchProductsByKeyword(
        keyword: String,
        page: Int
    ): List<ProductDTO>
}