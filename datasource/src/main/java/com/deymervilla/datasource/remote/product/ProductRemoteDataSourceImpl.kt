package com.deymervilla.datasource.remote.product

import com.deymervilla.network.api.ApiService
import javax.inject.Inject

class ProductRemoteDataSourceImpl @Inject constructor(
    private val apiService: ApiService
) : ProductRemoteDataSource {

    override suspend fun searchProductsByKeyword(
        keyword: String,
        page: Int
    ) = apiService.searchProductsByKeyword(
        keyword = keyword,
        page = page
    ).products
}