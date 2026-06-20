package com.deymervilla.datasource.local

import com.deymervilla.database.dao.SearchHistoryDao
import com.deymervilla.database.entities.SearchHistoryEntity
import com.deymervilla.datasource.local.searchhistory.SearchHistoryLocalDataSourceImpl
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
class SearchHistoryLocalDataSourceTest {

    @Mock
    private lateinit var searchHistoryDao: SearchHistoryDao

    private lateinit var dataSource: SearchHistoryLocalDataSourceImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        dataSource = SearchHistoryLocalDataSourceImpl(searchHistoryDao)
    }

    @Test
    fun `insert returns true when dao returns positive row id`() = runTest {
        val entry = dummySearchHistory("sony")
        `when`(searchHistoryDao.insert(entry)).thenReturn(1L)

        val result = dataSource.insert(entry)

        assertTrue(result)
    }

    @Test
    fun `insert returns false when dao returns non-positive row id`() = runTest {
        val entry = dummySearchHistory("sony")
        `when`(searchHistoryDao.insert(entry)).thenReturn(-1L)

        val result = dataSource.insert(entry)

        assertEquals(false, result)
    }

    @Test
    fun `fetchByKeyword returns matching entry`() = runTest {
        val entry = dummySearchHistory("sony")
        `when`(searchHistoryDao.fetchByKeyword("sony")).thenReturn(entry)

        val result = dataSource.fetchByKeyword("sony")

        assertEquals(entry, result)
    }

    @Test
    fun `fetchByKeyword returns null when not found`() = runTest {
        `when`(searchHistoryDao.fetchByKeyword("nonexistent")).thenReturn(null)

        val result = dataSource.fetchByKeyword("nonexistent")

        assertNull(result)
    }

    @Test
    fun `deleteByKeyword returns true when a row was deleted`() = runTest {
        `when`(searchHistoryDao.deleteByKeyword("sony")).thenReturn(1)

        val result = dataSource.deleteByKeyword("sony")

        assertTrue(result)
    }

    @Test
    fun `deleteByKeyword returns false when no row matched`() = runTest {
        `when`(searchHistoryDao.deleteByKeyword("nonexistent")).thenReturn(0)

        val result = dataSource.deleteByKeyword("nonexistent")

        assertEquals(false, result)
    }

    @Test
    fun `deleteAll delegates to dao`() = runTest {
        `when`(searchHistoryDao.deleteAll()).thenReturn(3)

        val result = dataSource.deleteAll()

        assertTrue(result)
    }

    private fun dummySearchHistory(keyword: String) = SearchHistoryEntity(
        keyword = keyword,
        searchedAt = System.currentTimeMillis(),
        thumbnailUrl = "https://example.com/$keyword.jpg"
    )
}