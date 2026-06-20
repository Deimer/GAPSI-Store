package com.deymervilla.database.constants

import com.deymervilla.database.BuildConfig

object DatabaseConstants {

    const val DATABASE_VERSION = 1
    const val KEY_NAME_DATABASE = BuildConfig.DATABASE_NAME

    object Tables {
        const val PRODUCT_TABLE = "product_table"
        const val SEARCH_HISTORY_TABLE = "search_history_table"
    }

    object Columns {
        // Common
        const val ID = "id"

        // Product
        const val TITLE = "title"
        const val PRICE = "price"
        const val WAS_PRICE = "was_price"
        const val THUMBNAIL_URL = "thumbnail_url"
        const val AVERAGE_RATING = "average_rating"
        const val NUMBER_OF_REVIEWS = "number_of_reviews"
        const val SELLER_NAME = "seller_name"
        const val CANONICAL_URL = "canonical_url"
        const val CATEGORY = "category"
        const val DESCRIPTION = "description"
        const val IS_OUT_OF_STOCK = "is_out_of_stock"
        const val SEARCH_KEYWORD = "search_keyword"
        const val PAGE = "page"

        // SearchHistory
        const val KEYWORD = "keyword"
        const val SEARCHED_AT = "searched_at"
        const val SEARCH_HISTORY_THUMBNAIL_URL = "search_history_thumbnail_url"
    }
}