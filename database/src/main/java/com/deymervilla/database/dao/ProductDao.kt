package com.deymervilla.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.deymervilla.database.constants.DatabaseConstants.Columns.PAGE
import com.deymervilla.database.constants.DatabaseConstants.Columns.SEARCH_KEYWORD
import com.deymervilla.database.constants.DatabaseConstants.Tables.PRODUCT_TABLE
import com.deymervilla.database.entities.ProductEntity

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(products: List<ProductEntity>): List<Long>

    @Query("SELECT * FROM $PRODUCT_TABLE WHERE $SEARCH_KEYWORD = :keyword ORDER BY $PAGE ASC")
    suspend fun fetchByKeyword(keyword: String): List<ProductEntity>

    @Query("SELECT * FROM $PRODUCT_TABLE WHERE $SEARCH_KEYWORD = :keyword AND $PAGE = :page")
    suspend fun fetchByKeywordAndPage(keyword: String, page: Int): List<ProductEntity>

    @Query("DELETE FROM $PRODUCT_TABLE WHERE $SEARCH_KEYWORD = :keyword")
    suspend fun deleteByKeyword(keyword: String): Int

    @Query("DELETE FROM $PRODUCT_TABLE")
    suspend fun deleteAll(): Int
}