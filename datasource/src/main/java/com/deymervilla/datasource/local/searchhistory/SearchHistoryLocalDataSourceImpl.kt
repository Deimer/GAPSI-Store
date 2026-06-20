package com.deymervilla.datasource.local.searchhistory

import com.deymervilla.database.dao.SearchHistoryDao
import com.deymervilla.database.entities.SearchHistoryEntity
import javax.inject.Inject

class SearchHistoryLocalDataSourceImpl @Inject constructor(
    private val searchHistoryDao: SearchHistoryDao
) : SearchHistoryLocalDataSource {

    override suspend fun insert(
        searchHistory: SearchHistoryEntity
    ) = searchHistoryDao.insert(searchHistory) > 0

    override fun fetchAll() =
        searchHistoryDao.fetchAll()

    override suspend fun fetchByKeyword(
        keyword: String
    ) = searchHistoryDao.fetchByKeyword(keyword)

    override suspend fun deleteByKeyword(
        keyword: String
    ) = searchHistoryDao.deleteByKeyword(keyword) > 0

    override suspend fun deleteAll() =
        searchHistoryDao.deleteAll() >= 0
}