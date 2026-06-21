package com.deymervilla.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.deymervilla.database.constants.DatabaseConstants.Columns
import com.deymervilla.database.constants.DatabaseConstants.Tables

@Entity(tableName = Tables.PRODUCT_TABLE)
data class ProductEntity(
    @PrimaryKey
    @ColumnInfo(name = Columns.ID)
    val id: String,
    @ColumnInfo(name = Columns.TITLE)
    val title: String,
    @ColumnInfo(name = Columns.PRICE)
    val price: Double,
    @ColumnInfo(name = Columns.WAS_PRICE)
    val wasPrice: Double?,
    @ColumnInfo(name = Columns.THUMBNAIL_URL)
    val thumbnailUrl: String,
    @ColumnInfo(name = Columns.AVERAGE_RATING)
    val averageRating: Double,
    @ColumnInfo(name = Columns.NUMBER_OF_REVIEWS)
    val numberOfReviews: Int,
    @ColumnInfo(name = Columns.SELLER_NAME)
    val sellerName: String,
    @ColumnInfo(name = Columns.CANONICAL_URL)
    val canonicalUrl: String,
    @ColumnInfo(name = Columns.CATEGORY)
    val category: String,
    @ColumnInfo(name = Columns.DESCRIPTION)
    val description: String,
    @ColumnInfo(name = Columns.IS_OUT_OF_STOCK)
    val isOutOfStock: Boolean,
    @ColumnInfo(name = Columns.SEARCH_KEYWORD)
    val searchKeyword: String,
    @ColumnInfo(name = Columns.PAGE)
    val page: Int
)