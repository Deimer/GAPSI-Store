package com.deymervilla.network.constants

object NetworkConstants {

    object DEFAULTS {
        const val DEFAULT_TIMEOUT = 15L
        const val DEFAULT_PAGE = 1
        const val SORT_BY_BEST_MATCH = "best_match"
        const val DEFAULT_EMPTY = ""
        const val SYMBOL_PRICE = "$"
        const val COMMA_SEPARATOR = ","
    }

    object PARAMETERS {
        const val PARAMETER_KEYWORD = "keyword"
        const val PARAMETER_PAGE = "page"
        const val PARAMETER_SORT_BY = "sortBy"
        const val HEADER_RAPID_API_KEY = "x-rapidapi-key"
        const val HEADER_RAPID_API_HOST = "x-rapidapi-host"
    }

    object HEADERS {
        const val RAPID_API_HOST_VALUE = "axesso-walmart-data-service.p.rapidapi.com"
    }

    object URLs {
        const val SEARCH_BY_KEYWORD_PATH = "wlm/walmart-search-by-keyword"
    }

    object JSON_PATH {
        const val FIELD_ITEM = "item"
        const val FIELD_PROPS = "props"
        const val FIELD_PAGE_PROPS = "pageProps"
        const val FIELD_INITIAL_DATA = "initialData"
        const val FIELD_SEARCH_RESULT = "searchResult"
        const val FIELD_ITEM_STACKS = "itemStacks"
        const val FIELD_ITEMS = "items"
    }
}