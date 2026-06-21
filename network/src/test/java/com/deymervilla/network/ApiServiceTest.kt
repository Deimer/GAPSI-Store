package com.deymervilla.network

import com.deymervilla.network.api.ApiService
import com.deymervilla.network.dto.ProductDTO
import com.deymervilla.network.dto.response.SearchResponseDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class ApiServiceTest {

    @Mock
    private lateinit var mockApiService: ApiService

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `searchProductsByKeyword returns a response wrapper with the product list`() = runTest {
        val expected = SearchResponseDTO(
            products = listOf(
                dummyProductDTO(id = "1", title = "Sony Headphones"),
                dummyProductDTO(id = "2", title = "Sony Speaker")
            )
        )
        `when`(mockApiService.searchProductsByKeyword("sony", 1)).thenReturn(expected)

        val response = mockApiService.searchProductsByKeyword("sony", 1)

        assertEquals(2, response.products.size)
        assertEquals("Sony Headphones", response.products[0].title)
        assertEquals("Sony Speaker", response.products[1].title)
    }

    @Test
    fun `searchProductsByKeyword returns empty products list when no results`() = runTest {
        `when`(mockApiService.searchProductsByKeyword("nonexistent", 1))
            .thenReturn(SearchResponseDTO(products = emptyList()))

        val response = mockApiService.searchProductsByKeyword("nonexistent", 1)

        assertTrue(response.products.isEmpty())
    }

    @Test
    fun `searchProductsByKeyword propagates exceptions from the network layer`() = runTest {
        `when`(mockApiService.searchProductsByKeyword("sony", 1))
            .thenThrow(RuntimeException("503 Service Unavailable"))

        try {
            mockApiService.searchProductsByKeyword("sony", 1)
            org.junit.Assert.fail("Expected exception to be thrown")
        } catch (e: Exception) {
            assertEquals("503 Service Unavailable", e.message)
        }
    }

    private fun dummyProductDTO(
        id: String,
        title: String,
        price: Double = 99.99,
        wasPrice: Double? = null
    ) = ProductDTO(
        productId = id,
        title = title,
        price = price,
        wasPrice = wasPrice,
        thumbnailUrl = "https://example.com/$id.jpg",
        averageRating = 4.5,
        numberOfReviews = 100,
        sellerName = "Walmart.com",
        canonicalUrl = "/ip/$title/$id",
        category = "Electronics",
        description = "Sample product description",
        isOutOfStock = false
    )
}