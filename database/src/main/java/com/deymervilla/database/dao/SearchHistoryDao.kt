package com.deymervilla.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.deymervilla.database.constants.DatabaseConstants.Columns.KEYWORD
import com.deymervilla.database.constants.DatabaseConstants.Columns.SEARCHED_AT
import com.deymervilla.database.constants.DatabaseConstants.Tables.SEARCH_HISTORY_TABLE
import com.deymervilla.database.entities.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(searchHistory: SearchHistoryEntity): Long

    @Query("SELECT * FROM $SEARCH_HISTORY_TABLE ORDER BY $SEARCHED_AT DESC")
    fun fetchAll(): Flow<List<SearchHistoryEntity>>

    @Query("SELECT * FROM $SEARCH_HISTORY_TABLE WHERE $KEYWORD = :keyword LIMIT 1")
    suspend fun fetchByKeyword(keyword: String): SearchHistoryEntity?

    @Query("DELETE FROM $SEARCH_HISTORY_TABLE WHERE $KEYWORD = :keyword")
    suspend fun deleteByKeyword(keyword: String): Int

    @Query("DELETE FROM $SEARCH_HISTORY_TABLE")
    suspend fun deleteAll(): Int
}