package com.deymervilla.gapsistore.features.home

import androidx.paging.PagingData
import com.deymervilla.domain.models.ProductModel
import com.deymervilla.domain.models.SearchHistoryModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow

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

data class HomeScreenAttributes(
    val query: String = "",
    val uiState: HomeUiState = HomeUiState.Idle,
    val searchHistory: List<SearchHistoryModel> = emptyList(),
    val products: Flow<PagingData<ProductModel>> = emptyFlow(),
    val hasActiveSearch: Boolean = false
)

class HomeScreenState {
    private val _attributes = MutableStateFlow(HomeScreenAttributes())
    val attributes: StateFlow<HomeScreenAttributes> = _attributes.asStateFlow()

    fun update(reducer: (HomeScreenAttributes) -> HomeScreenAttributes) {
        _attributes.value = reducer(_attributes.value)
    }
    fun current(): HomeScreenAttributes = _attributes.value
}

sealed interface HomeUiState {
    data object Idle : HomeUiState
    data object Loading : HomeUiState
    data object Success : HomeUiState
    data object ConnectionError : HomeUiState
    data class Error(val message: String) : HomeUiState
}