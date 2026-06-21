package com.deymervilla.domain.repositories.product

import android.util.Log
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
        private const val FIRST_PAGE = 1
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

    override suspend fun fetchFirstResultThumbnail(keyword: String): String? {
        return try {
            productRemoteDataSource
                .searchProductsByKeyword(keyword = keyword, page = FIRST_PAGE)
                .firstOrNull()
                ?.thumbnailUrl
        } catch (exception: Exception) {
            Log.e("ProductRepositoryImpl", "fetchFirstResultThumbnail: $exception")
            null
        }
    }
}