package com.deymervilla.gapsistore.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.deymervilla.gapsistore.di.IoDispatcher
import com.deymervilla.domain.usecase.product.FetchFirstResultThumbnailUseCase
import com.deymervilla.domain.usecase.product.SearchProductsUseCase
import com.deymervilla.domain.usecase.searchhistory.ClearSearchHistoryUseCase
import com.deymervilla.domain.usecase.searchhistory.DeleteSearchUseCase
import com.deymervilla.domain.usecase.searchhistory.FetchSearchHistoryUseCase
import com.deymervilla.domain.usecase.searchhistory.SaveSearchUseCase
import com.deymervilla.gapsistore.ui.failure
import com.deymervilla.gapsistore.ui.launchIn
import com.deymervilla.gapsistore.ui.success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val searchProductsUseCase: SearchProductsUseCase,
    private val fetchFirstResultThumbnailUseCase: FetchFirstResultThumbnailUseCase,
    private val saveSearchUseCase: SaveSearchUseCase,
    private val fetchSearchHistoryUseCase: FetchSearchHistoryUseCase,
    private val deleteSearchUseCase: DeleteSearchUseCase,
    private val clearSearchHistoryUseCase: ClearSearchHistoryUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel(), HomeScreenActions {

    private val screenState = HomeScreenState()
    val attributes = screenState.attributes

    init {
        observeSearchHistory()
    }

    override fun onQueryChange(query: String) {
        screenState.update { it.copy(query = query) }
    }

    override fun onSearch() {
        val keyword = screenState.current().query.trim()
        if (keyword.isEmpty()) return
        executeSearch(keyword)
    }

    override fun onSearchHistoryItemClick(keyword: String) {
        screenState.update { it.copy(query = keyword) }
        executeSearch(keyword)
    }

    override fun onDeleteSearchHistoryItem(keyword: String) {
        viewModelScope.launch(ioDispatcher) {
            deleteSearchUseCase(keyword)
        }
    }

    override fun onClearSearchHistory() {
        viewModelScope.launch(ioDispatcher) {
            clearSearchHistoryUseCase()
        }
    }

    override fun onProductClick(productId: String) {}

    private fun executeSearch(keyword: String) {
        val pagingFlow = searchProductsUseCase(
            keyword
        ).cachedIn(viewModelScope)

        screenState.update {
            it.copy(
                uiState = HomeUiState.Success,
                hasActiveSearch = true,
                products = pagingFlow
            )
        }

        persistSearchEntry(keyword)
    }

    private fun persistSearchEntry(keyword: String) {
        viewModelScope.launch(ioDispatcher) {
            val thumbnailUrl = fetchFirstResultThumbnailUseCase(keyword)
            saveSearchUseCase(keyword, thumbnailUrl)
        }
    }

    private fun observeSearchHistory() {
        fetchSearchHistoryUseCase()
            .success { history ->
                screenState.update { it.copy(searchHistory = history) }
            }.failure {

            }.launchIn(viewModelScope, ioDispatcher)
    }
}