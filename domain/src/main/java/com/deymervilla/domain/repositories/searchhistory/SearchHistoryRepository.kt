package com.deymervilla.domain.repositories.searchhistory

import com.deymervilla.domain.models.SearchHistoryModel
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {

    suspend fun saveSearch(
        keyword: String,
        thumbnailUrl: String?
    ): Boolean

    fun fetchSearchHistory(): Flow<Result<List<SearchHistoryModel>>>

    suspend fun deleteSearch(
        keyword: String
    ): Boolean

    suspend fun clearSearchHistory(): Boolean
}