package com.deymervilla.gapsistore.features.home

interface HomeScreenActions {

    fun onQueryChange(
        query: String
    )

    fun onSearch()

    fun onSearchHistoryItemClick(
        keyword: String
    )

    fun onDeleteSearchHistoryItem(
        keyword: String
    )

    fun onClearSearchHistory()

    fun onProductClick(productId: String)

    fun onFirstResultLoaded(
        keyword: String,
        thumbnailUrl: String?
    )
}