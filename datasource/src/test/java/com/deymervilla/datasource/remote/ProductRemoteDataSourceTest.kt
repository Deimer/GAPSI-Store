package com.deymervilla.datasource.remote

import com.deymervilla.datasource.remote.product.ProductRemoteDataSourceImpl
import com.deymervilla.network.api.ApiService
import com.deymervilla.network.dto.ProductDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class ProductRemoteDataSourceTest {

    @Mock
    private lateinit var apiService: ApiService

    private lateinit var dataSource: ProductRemoteDataSourceImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        dataSource = ProductRemoteDataSourceImpl(apiService)
    }

    @Test
    fun `searchProductsByKeyword delegates to apiService with correct params`() = runTest {
        val expected = listOf(dummyProductDTO(id = "1", title = "Sony Headphones"))
        `when`(apiService.searchProductsByKeyword(keyword = "sony", page = 1)).thenReturn(expected)

        val result = dataSource.searchProductsByKeyword(keyword = "sony", page = 1)

        assertEquals(expected, result)
    }

    @Test
    fun `searchProductsByKeyword returns empty list when api returns empty`() = runTest {
        `when`(apiService.searchProductsByKeyword(keyword = "nonexistent", page = 1))
            .thenReturn(emptyList())

        val result = dataSource.searchProductsByKeyword(keyword = "nonexistent", page = 1)

        assertEquals(emptyList<ProductDTO>(), result)
    }

    private fun dummyProductDTO(
        id: String,
        title: String
    ) = ProductDTO(
        productId = id,
        title = title,
        price = 99.99,
        wasPrice = null,
        thumbnailUrl = "https://example.com/$id.jpg",
        averageRating = 4.5,
        numberOfReviews = 100,
        sellerName = "Walmart.com",
        canonicalUrl = "/ip/$title/$id",
        category = "Electronics",
        description = "Sample description",
        isOutOfStock = false
    )
}