package com.deymervilla.domain.repositories

import com.deymervilla.database.entities.SearchHistoryEntity
import com.deymervilla.datasource.local.searchhistory.SearchHistoryLocalDataSource
import com.deymervilla.domain.repositories.searchhistory.SearchHistoryRepositoryImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class SearchHistoryRepositoryImplTest {

    private val searchHistoryLocalDataSource: SearchHistoryLocalDataSource = mock()

    private lateinit var repository: SearchHistoryRepositoryImpl

    @Before
    fun setUp() {
        repository = SearchHistoryRepositoryImpl(searchHistoryLocalDataSource)
    }

    @Test
    fun `saveSearch delegates to local data source with current timestamp`() = runTest {
        whenever(searchHistoryLocalDataSource.insert(any())).thenReturn(true)

        val result = repository.saveSearch("sony", "https://example.com/sony.jpg")

        assertTrue(result)
    }

    @Test
    fun `fetchSearchHistory emits Result success with entities mapped to models`() = runTest {
        val entities = listOf(
            SearchHistoryEntity(keyword = "sony", searchedAt = 1000L, thumbnailUrl = "img1.jpg"),
            SearchHistoryEntity(keyword = "nintendo", searchedAt = 2000L, thumbnailUrl = "img2.jpg")
        )
        whenever(searchHistoryLocalDataSource.fetchAll()).thenReturn(flowOf(entities))

        val result = repository.fetchSearchHistory().first()

        assertTrue(result.isSuccess)
        val history = result.getOrNull().orEmpty()
        assertEquals(2, history.size)
        assertEquals("sony", history[0].keyword)
        assertEquals("img1.jpg", history[0].thumbnailUrl)
    }

    @Test
    fun `fetchSearchHistory emits Result success with empty list when there is no history`() = runTest {
        whenever(searchHistoryLocalDataSource.fetchAll()).thenReturn(flowOf(emptyList()))

        val result = repository.fetchSearchHistory().first()

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull().orEmpty().isEmpty())
    }

    @Test
    fun `fetchSearchHistory emits Result failure when the underlying flow throws`() = runTest {
        val expectedException = RuntimeException("Database error")
        whenever(searchHistoryLocalDataSource.fetchAll()).thenReturn(
            flow { throw expectedException }
        )

        val result = repository.fetchSearchHistory().first()

        assertTrue(result.isFailure)
        assertEquals(expectedException, result.exceptionOrNull())
    }

    @Test
    fun `deleteSearch delegates to local data source`() = runTest {
        whenever(searchHistoryLocalDataSource.deleteByKeyword("sony")).thenReturn(true)

        val result = repository.deleteSearch("sony")

        assertTrue(result)
    }

    @Test
    fun `clearSearchHistory delegates to local data source`() = runTest {
        whenever(searchHistoryLocalDataSource.deleteAll()).thenReturn(true)

        val result = repository.clearSearchHistory()

        assertTrue(result)
    }
}