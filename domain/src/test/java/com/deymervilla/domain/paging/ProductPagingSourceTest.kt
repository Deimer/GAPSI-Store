package com.deymervilla.domain.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.deymervilla.database.entities.ProductEntity
import com.deymervilla.datasource.local.product.ProductLocalDataSource
import com.deymervilla.datasource.remote.product.ProductRemoteDataSource
import com.deymervilla.domain.models.ProductModel
import com.deymervilla.network.dto.ProductDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class ProductPagingSourceTest {

    private val productRemoteDataSource: ProductRemoteDataSource = mock()
    private val productLocalDataSource: ProductLocalDataSource = mock()

    private lateinit var pagingSource: ProductPagingSource

    @Before
    fun setUp() {
        pagingSource = ProductPagingSource(
            keyword = KEYWORD,
            productRemoteDataSource = productRemoteDataSource,
            productLocalDataSource = productLocalDataSource
        )
    }

    @Test
    fun `load returns Page with products mapped from persisted entities`() = runTest {
        val dtos = listOf(dummyProductDTO("1"), dummyProductDTO("2"))
        val persistedEntities = listOf(dummyProductEntity("1"), dummyProductEntity("2"))

        whenever(productRemoteDataSource.searchProductsByKeyword(KEYWORD, 1)).thenReturn(dtos)
        whenever(productLocalDataSource.insert(any())).thenReturn(true)
        whenever(productLocalDataSource.fetchByKeywordAndPage(KEYWORD, 1)).thenReturn(persistedEntities)

        val result = pagingSource.load(refreshParams(key = null))

        assertTrue("Expected Page but got $result", result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(2, page.data.size)
        assertEquals("1", page.data[0].id)
        assertEquals("2", page.data[1].id)
    }

    @Test
    fun `load calls insert before reading back from local data source`() = runTest {
        val dtos = listOf(dummyProductDTO("1"))
        whenever(productRemoteDataSource.searchProductsByKeyword(KEYWORD, 1)).thenReturn(dtos)
        whenever(productLocalDataSource.insert(any())).thenReturn(true)
        whenever(productLocalDataSource.fetchByKeywordAndPage(KEYWORD, 1))
            .thenReturn(listOf(dummyProductEntity("1")))

        pagingSource.load(refreshParams(key = null))

        verify(productLocalDataSource).insert(any())
        verify(productLocalDataSource).fetchByKeywordAndPage(KEYWORD, 1)
    }

    @Test
    fun `load returns Page with null prevKey on first page`() = runTest {
        whenever(productRemoteDataSource.searchProductsByKeyword(KEYWORD, 1))
            .thenReturn(listOf(dummyProductDTO("1")))
        whenever(productLocalDataSource.insert(any())).thenReturn(true)
        whenever(productLocalDataSource.fetchByKeywordAndPage(KEYWORD, 1))
            .thenReturn(listOf(dummyProductEntity("1")))

        val result = pagingSource.load(refreshParams(key = 1)) as PagingSource.LoadResult.Page

        assertNull(result.prevKey)
    }

    @Test
    fun `load returns Page with prevKey when not on first page`() = runTest {
        whenever(productRemoteDataSource.searchProductsByKeyword(KEYWORD, 3))
            .thenReturn(listOf(dummyProductDTO("1")))
        whenever(productLocalDataSource.insert(any())).thenReturn(true)
        whenever(productLocalDataSource.fetchByKeywordAndPage(KEYWORD, 3))
            .thenReturn(listOf(dummyProductEntity("1")))

        val result = pagingSource.load(appendParams(key = 3)) as PagingSource.LoadResult.Page

        assertEquals(2, result.prevKey)
    }

    @Test
    fun `load returns null nextKey when remote results are empty`() = runTest {
        whenever(productRemoteDataSource.searchProductsByKeyword(KEYWORD, 5))
            .thenReturn(emptyList())

        val result = pagingSource.load(appendParams(key = 5)) as PagingSource.LoadResult.Page

        assertNull(result.nextKey)
        assertTrue(result.data.isEmpty())
    }

    @Test
    fun `load does not call insert nor fetch when remote results are empty`() = runTest {
        whenever(productRemoteDataSource.searchProductsByKeyword(KEYWORD, 1))
            .thenReturn(emptyList())

        pagingSource.load(refreshParams(key = null))

        verify(productLocalDataSource, never()).insert(any())
        verify(productLocalDataSource, never()).fetchByKeywordAndPage(any(), any())
    }

    @Test
    fun `load returns Error when remote data source throws generic exception`() = runTest {
        whenever(productRemoteDataSource.searchProductsByKeyword(KEYWORD, 1))
            .thenThrow(RuntimeException("Unexpected error"))

        val result = pagingSource.load(refreshParams(key = null))

        assertTrue(result is PagingSource.LoadResult.Error)
    }

    @Test
    fun `load uses starting page 1 when key is null`() = runTest {
        whenever(productRemoteDataSource.searchProductsByKeyword(KEYWORD, 1))
            .thenReturn(listOf(dummyProductDTO("1")))
        whenever(productLocalDataSource.insert(any())).thenReturn(true)
        whenever(productLocalDataSource.fetchByKeywordAndPage(KEYWORD, 1))
            .thenReturn(listOf(dummyProductEntity("1")))

        pagingSource.load(refreshParams(key = null))

        verify(productRemoteDataSource).searchProductsByKeyword(KEYWORD, 1)
    }

    @Test
    fun `getRefreshKey returns null when anchorPosition is null`() {
        val state = PagingState<Int, ProductModel>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 40),
            leadingPlaceholderCount = 0
        )

        assertNull(pagingSource.getRefreshKey(state))
    }

    private fun refreshParams(key: Int?) =
        PagingSource.LoadParams.Refresh(key = key, loadSize = 40, placeholdersEnabled = false)

    private fun appendParams(key: Int) =
        PagingSource.LoadParams.Append(key = key, loadSize = 40, placeholdersEnabled = false)

    private fun dummyProductDTO(id: String) = ProductDTO(
        productId = id,
        title = "Sony Product $id",
        price = 99.99,
        wasPrice = null,
        thumbnailUrl = "https://example.com/$id.jpg",
        averageRating = 4.5,
        numberOfReviews = 100,
        sellerName = "Walmart.com",
        canonicalUrl = "/ip/sony/$id",
        category = "Electronics",
        description = "Sample description",
        isOutOfStock = false
    )

    private fun dummyProductEntity(id: String) = ProductEntity(
        id = id,
        title = "Sony Product $id",
        price = 99.99,
        wasPrice = null,
        thumbnailUrl = "https://example.com/$id.jpg",
        averageRating = 4.5,
        numberOfReviews = 100,
        sellerName = "Walmart.com",
        canonicalUrl = "/ip/sony/$id",
        category = "Electronics",
        description = "Sample description",
        isOutOfStock = false,
        searchKeyword = KEYWORD,
        page = 1
    )

    private companion object {
        const val KEYWORD = "sony"
    }
}