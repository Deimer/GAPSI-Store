package com.deymervilla.datasource.local

import com.deymervilla.database.dao.ProductDao
import com.deymervilla.database.entities.ProductEntity
import com.deymervilla.datasource.local.product.ProductLocalDataSourceImpl
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
class ProductLocalDataSourceTest {

    @Mock
    private lateinit var productDao: ProductDao

    private lateinit var dataSource: ProductLocalDataSourceImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        dataSource = ProductLocalDataSourceImpl(productDao)
    }

    @Test
    fun `fetchByKeyword delegates to dao`() = runTest {
        val expected = listOf(dummyProductEntity(id = "1", keyword = "sony"))
        `when`(productDao.fetchByKeyword("sony")).thenReturn(expected)

        val result = dataSource.fetchByKeyword("sony")

        assertEquals(expected, result)
    }

    @Test
    fun `fetchByKeywordAndPage delegates to dao`() = runTest {
        val expected = listOf(dummyProductEntity(id = "1", keyword = "sony"))
        `when`(productDao.fetchByKeywordAndPage("sony", 2)).thenReturn(expected)

        val result = dataSource.fetchByKeywordAndPage("sony", 2)

        assertEquals(expected, result)
    }

    @Test
    fun `insert returns true when all rows inserted successfully`() = runTest {
        val products = listOf(dummyProductEntity(id = "1", keyword = "sony"))
        `when`(productDao.insert(products)).thenReturn(listOf(1L))

        val result = dataSource.insert(products)

        assertTrue(result)
    }

    @Test
    fun `insert returns false when dao returns empty result`() = runTest {
        val products = listOf(dummyProductEntity(id = "1", keyword = "sony"))
        `when`(productDao.insert(products)).thenReturn(emptyList())

        val result = dataSource.insert(products)

        assertEquals(false, result)
    }

    @Test
    fun `insert returns false when any row fails to insert`() = runTest {
        val products = listOf(
            dummyProductEntity(id = "1", keyword = "sony"),
            dummyProductEntity(id = "2", keyword = "sony")
        )
        `when`(productDao.insert(products)).thenReturn(listOf(1L, -1L))

        val result = dataSource.insert(products)

        assertEquals(false, result)
    }

    @Test
    fun `deleteByKeyword delegates to dao`() = runTest {
        `when`(productDao.deleteByKeyword("sony")).thenReturn(2)

        val result = dataSource.deleteByKeyword("sony")

        assertTrue(result)
    }

    @Test
    fun `deleteAll delegates to dao`() = runTest {
        `when`(productDao.deleteAll()).thenReturn(5)

        val result = dataSource.deleteAll()

        assertTrue(result)
    }

    private fun dummyProductEntity(
        id: String,
        keyword: String
    ) = ProductEntity(
        id = id,
        title = "Sample Product",
        price = 99.99,
        wasPrice = null,
        thumbnailUrl = "https://example.com/$id.jpg",
        averageRating = 4.5,
        numberOfReviews = 100,
        sellerName = "Walmart.com",
        canonicalUrl = "/ip/sample/$id",
        category = "Electronics",
        description = "Sample description",
        isOutOfStock = false,
        searchKeyword = keyword,
        page = 1
    )
}