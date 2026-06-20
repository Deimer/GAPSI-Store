package com.deymervilla.datasource.local.searchhistory

import com.deymervilla.database.entities.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

interface SearchHistoryLocalDataSource {

    suspend fun insert(
        searchHistory: SearchHistoryEntity
    ): Boolean

    fun fetchAll(): Flow<List<SearchHistoryEntity>>

    suspend fun fetchByKeyword(
        keyword: String
    ): SearchHistoryEntity?

    suspend fun deleteByKeyword(
        keyword: String
    ): Boolean

    suspend fun deleteAll(): Boolean
}