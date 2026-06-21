package com.deymervilla.domain.repositories.product

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.deymervilla.datasource.local.product.ProductLocalDataSource
import com.deymervilla.datasource.remote.product.ProductRemoteDataSource
import com.deymervilla.domain.paging.ProductPagingSource
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productRemoteDataSource: ProductRemoteDataSource,
    private val productLocalDataSource: ProductLocalDataSource
) : ProductRepository {

    companion object {
        private const val PAGE_SIZE = 40
        private const val PREFETCH_DISTANCE = 10
    }

    override fun searchProductsByKeyword(keyword: String) = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            prefetchDistance = PREFETCH_DISTANCE,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            ProductPagingSource(
                keyword = keyword,
                productRemoteDataSource = productRemoteDataSource,
                productLocalDataSource = productLocalDataSource
            )
        }
    ).flow
}