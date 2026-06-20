package com.deymervilla.domain.repositories

import com.deymervilla.database.entities.SearchHistoryEntity
import com.deymervilla.datasource.local.searchhistory.SearchHistoryLocalDataSource
import com.deymervilla.domain.repositories.searchhistory.SearchHistoryRepositoryImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any

@ExperimentalCoroutinesApi
class SearchHistoryRepositoryImplTest {

    @Mock
    private lateinit var searchHistoryLocalDataSource: SearchHistoryLocalDataSource

    private lateinit var repository: SearchHistoryRepositoryImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = SearchHistoryRepositoryImpl(searchHistoryLocalDataSource)
    }

    @Test
    fun `saveSearch delegates to local data source with current timestamp`() = runTest {
        `when`(searchHistoryLocalDataSource.insert(any())).thenReturn(true)

        val result = repository.saveSearch("sony", "https://example.com/sony.jpg")

        assertTrue(result)
    }

    @Test
    fun `fetchSearchHistory maps entities to models`() = runTest {
        val entities = listOf(
            SearchHistoryEntity(keyword = "sony", searchedAt = 1000L, thumbnailUrl = "img1.jpg"),
            SearchHistoryEntity(keyword = "nintendo", searchedAt = 2000L, thumbnailUrl = "img2.jpg")
        )
        `when`(searchHistoryLocalDataSource.fetchAll()).thenReturn(flowOf(entities))

        val result = repository.fetchSearchHistory().first()

        assertEquals(2, result.size)
        assertEquals("sony", result[0].keyword)
        assertEquals("img1.jpg", result[0].thumbnailUrl)
    }

    @Test
    fun `deleteSearch delegates to local data source`() = runTest {
        `when`(searchHistoryLocalDataSource.deleteByKeyword("sony")).thenReturn(true)

        val result = repository.deleteSearch("sony")

        assertTrue(result)
    }

    @Test
    fun `clearSearchHistory delegates to local data source`() = runTest {
        `when`(searchHistoryLocalDataSource.deleteAll()).thenReturn(true)

        val result = repository.clearSearchHistory()

        assertTrue(result)
    }
}