package com.deymervilla.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.deymervilla.database.entities.ProductEntity
import com.deymervilla.database.entities.SearchHistoryEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoomDaoTest {

    private lateinit var database: RoomDatabase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RoomDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndFetchProductsByKeyword() = runBlocking {
        val products = listOf(
            dummyProduct(id = "1", keyword = "sony", page = 1),
            dummyProduct(id = "2", keyword = "sony", page = 1)
        )
        database.getProductDao().insert(products)

        val result = database.getProductDao().fetchByKeyword("sony")

        assertEquals(2, result.size)
    }

    @Test
    fun fetchByKeywordAndPage_returnsOnlyMatchingPage() = runBlocking {
        val products = listOf(
            dummyProduct(id = "1", keyword = "sony", page = 1),
            dummyProduct(id = "2", keyword = "sony", page = 2)
        )
        database.getProductDao().insert(products)

        val result = database.getProductDao().fetchByKeywordAndPage("sony", 1)

        assertEquals(1, result.size)
        assertEquals("1", result.first().id)
    }

    @Test
    fun fetchByKeyword_returnsEmpty_whenNoMatch() = runBlocking {
        val result = database.getProductDao().fetchByKeyword("nonexistent")

        assertTrue(result.isEmpty())
    }

    @Test
    fun insertProduct_replacesOnConflict() = runBlocking {
        val original = dummyProduct(id = "1", keyword = "sony", page = 1, title = "Original Title")
        val updated = dummyProduct(id = "1", keyword = "sony", page = 1, title = "Updated Title")

        database.getProductDao().insert(listOf(original))
        database.getProductDao().insert(listOf(updated))

        val result = database.getProductDao().fetchByKeyword("sony")

        assertEquals(1, result.size)
        assertEquals("Updated Title", result.first().title)
    }

    @Test
    fun deleteByKeyword_removesOnlyMatchingProducts() = runBlocking {
        database.getProductDao().insert(
            listOf(
                dummyProduct(id = "1", keyword = "sony", page = 1),
                dummyProduct(id = "2", keyword = "samsung", page = 1)
            )
        )

        database.getProductDao().deleteByKeyword("sony")

        assertTrue(database.getProductDao().fetchByKeyword("sony").isEmpty())
        assertEquals(1, database.getProductDao().fetchByKeyword("samsung").size)
    }

    @Test
    fun deleteAllProducts_clearsTable() = runBlocking {
        database.getProductDao().insert(
            listOf(
                dummyProduct(id = "1", keyword = "sony", page = 1),
                dummyProduct(id = "2", keyword = "samsung", page = 1)
            )
        )

        database.getProductDao().deleteAll()

        assertTrue(database.getProductDao().fetchByKeyword("sony").isEmpty())
        assertTrue(database.getProductDao().fetchByKeyword("samsung").isEmpty())
    }

    @Test
    fun insertAndFetchSearchHistory() = runBlocking {
        database.getSearchHistoryDao().insert(dummySearchHistory(keyword = "sony"))
        database.getSearchHistoryDao().insert(dummySearchHistory(keyword = "nintendo"))

        val result = database.getSearchHistoryDao().fetchAll().first()

        assertEquals(2, result.size)
    }

    @Test
    fun fetchAllSearchHistory_isOrderedByMostRecentFirst() = runBlocking {
        database.getSearchHistoryDao().insert(dummySearchHistory(keyword = "sony", searchedAt = 1000L))
        database.getSearchHistoryDao().insert(dummySearchHistory(keyword = "nintendo", searchedAt = 2000L))

        val result = database.getSearchHistoryDao().fetchAll().first()

        assertEquals("nintendo", result.first().keyword)
        assertEquals("sony", result.last().keyword)
    }

    @Test
    fun fetchByKeyword_returnsMatchingEntry() = runBlocking {
        database.getSearchHistoryDao().insert(dummySearchHistory(keyword = "sony"))

        val result = database.getSearchHistoryDao().fetchByKeyword("sony")

        assertEquals("sony", result?.keyword)
    }

    @Test
    fun fetchByKeyword_returnsNull_whenNotFound() = runBlocking {
        val result = database.getSearchHistoryDao().fetchByKeyword("nonexistent")

        assertNull(result)
    }

    @Test
    fun deleteByKeyword_removesOnlyMatchingEntry() = runBlocking {
        database.getSearchHistoryDao().insert(dummySearchHistory(keyword = "sony"))
        database.getSearchHistoryDao().insert(dummySearchHistory(keyword = "nintendo"))

        database.getSearchHistoryDao().deleteByKeyword("sony")

        val result = database.getSearchHistoryDao().fetchAll().first()

        assertEquals(1, result.size)
        assertEquals("nintendo", result.first().keyword)
    }

    @Test
    fun deleteAllSearchHistory_clearsTable() = runBlocking {
        database.getSearchHistoryDao().insert(dummySearchHistory(keyword = "sony"))
        database.getSearchHistoryDao().insert(dummySearchHistory(keyword = "nintendo"))

        database.getSearchHistoryDao().deleteAll()

        assertTrue(database.getSearchHistoryDao().fetchAll().first().isEmpty())
    }

    private fun dummyProduct(
        id: String,
        keyword: String,
        page: Int,
        title: String = "Sample Product"
    ) = ProductEntity(
        id = id,
        title = title,
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
        page = page
    )

    private fun dummySearchHistory(
        keyword: String,
        searchedAt: Long = System.currentTimeMillis()
    ) = SearchHistoryEntity(
        keyword = keyword,
        searchedAt = searchedAt,
        thumbnailUrl = "https://example.com/search/$keyword.jpg"
    )
}