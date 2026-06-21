package com.deymervilla.domain.repositories.searchhistory

import com.deymervilla.database.entities.SearchHistoryEntity
import com.deymervilla.datasource.local.searchhistory.SearchHistoryLocalDataSource
import com.deymervilla.domain.mappers.toModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchHistoryRepositoryImpl @Inject constructor(
    private val searchHistoryLocalDataSource: SearchHistoryLocalDataSource
) : SearchHistoryRepository {

    override suspend fun saveSearch(
        keyword: String,
        thumbnailUrl: String?
    ): Boolean {
        val entry = SearchHistoryEntity(
            keyword = keyword,
            searchedAt = System.currentTimeMillis(),
            thumbnailUrl = thumbnailUrl
        )
        return searchHistoryLocalDataSource.insert(entry)
    }

    override fun fetchSearchHistory() = searchHistoryLocalDataSource
        .fetchAll().map { entities ->
            Result.success(entities.map { it.toModel() })
        }.catch { throwable ->
            emit(Result.failure(throwable))
        }

    override suspend fun deleteSearch(
        keyword: String
    ) = searchHistoryLocalDataSource.deleteByKeyword(
        keyword
    )

    override suspend fun clearSearchHistory() =
        searchHistoryLocalDataSource.deleteAll()
}