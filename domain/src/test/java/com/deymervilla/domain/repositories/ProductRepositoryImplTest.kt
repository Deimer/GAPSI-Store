package com.deymervilla.domain.repositories

import androidx.paging.testing.asSnapshot
import com.deymervilla.datasource.local.product.ProductLocalDataSource
import com.deymervilla.datasource.remote.product.ProductRemoteDataSource
import com.deymervilla.domain.mappers.toEntity
import com.deymervilla.domain.repositories.product.ProductRepositoryImpl
import com.deymervilla.network.dto.ProductDTO
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
class ProductRepositoryImplTest {

    @Mock
    private lateinit var productRemoteDataSource: ProductRemoteDataSource

    @Mock
    private lateinit var productLocalDataSource: ProductLocalDataSource

    private lateinit var repository: ProductRepositoryImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = ProductRepositoryImpl(productRemoteDataSource, productLocalDataSource)
    }

    @Test
    fun `searchProductsByKeyword aggregates multiple pages until an empty page is reached`() = runTest {
        val dtoPage1 = listOf(dummyProductDTO("1"), dummyProductDTO("2"))
        val entitiesPage1 = dtoPage1.map { it.toEntity(searchKeyword = "sony", page = 1) }
        val dtoPage2 = listOf(dummyProductDTO("3"))
        val entitiesPage2 = dtoPage2.map { it.toEntity(searchKeyword = "sony", page = 2) }

        `when`(productRemoteDataSource.searchProductsByKeyword("sony", 1)).thenReturn(dtoPage1)
        `when`(productLocalDataSource.insert(entitiesPage1)).thenReturn(true)
        `when`(productLocalDataSource.fetchByKeywordAndPage("sony", 1)).thenReturn(entitiesPage1)

        `when`(productRemoteDataSource.searchProductsByKeyword("sony", 2)).thenReturn(dtoPage2)
        `when`(productLocalDataSource.insert(entitiesPage2)).thenReturn(true)
        `when`(productLocalDataSource.fetchByKeywordAndPage("sony", 2)).thenReturn(entitiesPage2)

        `when`(productRemoteDataSource.searchProductsByKeyword("sony", 3)).thenReturn(emptyList())

        val snapshot = repository.searchProductsByKeyword("sony").asSnapshot {
            scrollTo(index = 2)
        }

        assertEquals(3, snapshot.size)
        assertEquals(listOf("1", "2", "3"), snapshot.map { it.id })
    }

    @Test
    fun `searchProductsByKeyword emits empty snapshot when the API returns no results`() = runTest {
        `when`(productRemoteDataSource.searchProductsByKeyword("nonexistent", 1))
            .thenReturn(emptyList())

        val snapshot = repository.searchProductsByKeyword("nonexistent").asSnapshot()

        assertTrue(snapshot.isEmpty())
    }

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
}