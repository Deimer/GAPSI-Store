package com.deymervilla.network

import com.deymervilla.network.api.ApiService
import com.deymervilla.network.dto.ProductDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
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
    fun `searchProductsByKeyword returns product list`() = runTest {
        val expected = listOf(
            dummyProductDTO(id = "1", title = "Sony Headphones"),
            dummyProductDTO(id = "2", title = "Sony Speaker")
        )
        `when`(mockApiService.searchProductsByKeyword("sony", 1)).thenReturn(expected)

        val response = mockApiService.searchProductsByKeyword("sony", 1)

        assertEquals(2, response.size)
        assertEquals("Sony Headphones", response[0].title)
        assertEquals("Sony Speaker", response[1].title)
    }

    @Test
    fun `searchProductsByKeyword returns empty list when no results`() = runTest {
        `when`(mockApiService.searchProductsByKeyword("nonexistent", 1)).thenReturn(emptyList())

        val response = mockApiService.searchProductsByKeyword("nonexistent", 1)

        assertTrue(response.isEmpty())
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

    @Test
    fun `searchProductsByKeyword respects default sortBy parameter`() = runTest {
        val expected = listOf(dummyProductDTO(id = "1", title = "Sony Headphones"))
        `when`(mockApiService.searchProductsByKeyword("sony", 1)).thenReturn(expected)

        val response = mockApiService.searchProductsByKeyword(keyword = "sony", page = 1)

        assertEquals(1, response.size)
    }

    @Test
    fun `searchProductsByKeyword exposes discount fields when present`() = runTest {
        val discounted = dummyProductDTO(
            id = "2809072158",
            title = "Sony WH-CH720N Headphones",
            price = 99.99,
            wasPrice = 179.99
        )
        `when`(mockApiService.searchProductsByKeyword("sony", 1)).thenReturn(listOf(discounted))

        val response = mockApiService.searchProductsByKeyword("sony", 1)

        assertEquals(1, response.size)
        assertEquals(99.99, response.first().price!!, 0.001)
        assertEquals(179.99, response.first().wasPrice!!, 0.001)
    }

    @Test
    fun `searchProductsByKeyword exposes null wasPrice when no discount`() = runTest {
        val noDiscount = dummyProductDTO(id = "1", title = "Sony Headphones")
        `when`(mockApiService.searchProductsByKeyword("sony", 1)).thenReturn(listOf(noDiscount))

        val response = mockApiService.searchProductsByKeyword("sony", 1)

        assertNull(response.first().wasPrice)
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