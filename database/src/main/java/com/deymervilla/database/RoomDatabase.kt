package com.deymervilla.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.deymervilla.database.constants.DatabaseConstants.DATABASE_VERSION
import com.deymervilla.database.dao.ProductDao
import com.deymervilla.database.dao.SearchHistoryDao
import com.deymervilla.database.entities.ProductEntity
import com.deymervilla.database.entities.SearchHistoryEntity

@Database(
    entities = [
        ProductEntity::class,
        SearchHistoryEntity::class
   ],
    version = DATABASE_VERSION
)
abstract class RoomDatabase : RoomDatabase() {

    abstract fun getProductDao(): ProductDao

    abstract fun getSearchHistoryDao(): SearchHistoryDao
}