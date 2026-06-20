package com.deymervilla.domain.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.deymervilla.datasource.local.product.ProductLocalDataSource
import com.deymervilla.datasource.remote.product.ProductRemoteDataSource
import com.deymervilla.domain.mappers.toEntity
import com.deymervilla.domain.mappers.toModel
import com.deymervilla.domain.models.ProductModel
import java.io.IOException

class ProductPagingSource(
    private val keyword: String,
    private val productRemoteDataSource: ProductRemoteDataSource,
    private val productLocalDataSource: ProductLocalDataSource
) : PagingSource<Int, ProductModel>() {

    companion object {
        private const val STARTING_PAGE = 1
    }

    override fun getRefreshKey(state: PagingState<Int, ProductModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductModel> {
        val page = params.key ?: STARTING_PAGE

        return try {
            val productDTOs = productRemoteDataSource.searchProductsByKeyword(
                keyword = keyword,
                page = page
            )
            val entities = productDTOs.map { it.toEntity(searchKeyword = keyword, page = page) }
            if (entities.isNotEmpty()) {
                productLocalDataSource.insert(entities)
            }
            val persistedEntities = if (entities.isNotEmpty()) {
                productLocalDataSource.fetchByKeywordAndPage(keyword = keyword, page = page)
            } else {
                emptyList()
            }
            val products = persistedEntities.map { it.toModel() }

            LoadResult.Page(
                data = products,
                prevKey = if (page == STARTING_PAGE) null else page - 1,
                nextKey = if (productDTOs.isEmpty()) null else page + 1
            )
        } catch (ioException: IOException) {
            LoadResult.Error(ioException)
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }
}