package com.deymervilla.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.deymervilla.database.constants.DatabaseConstants.Columns
import com.deymervilla.database.constants.DatabaseConstants.Tables

@Entity(tableName = Tables.SEARCH_HISTORY_TABLE)
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Columns.ID)
    val id: Int = 0,
    @ColumnInfo(name = Columns.KEYWORD)
    val keyword: String,
    @ColumnInfo(name = Columns.SEARCHED_AT)
    val searchedAt: Long,
    @ColumnInfo(name = Columns.SEARCH_HISTORY_THUMBNAIL_URL)
    val thumbnailUrl: String?
)