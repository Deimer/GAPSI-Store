package com.deymervilla.gapsistore.features.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.deymervilla.ds.components.ProductCardCompose
import com.deymervilla.ds.components.RecentSearchesHorizontalCompose
import com.deymervilla.ds.components.SearchFieldCompose
import com.deymervilla.ds.screens.ErrorScreenCompose
import com.deymervilla.ds.screens.LoadingScreenCompose
import com.deymervilla.ds.uimodel.SearchItemUIModel

@Composable
fun HomeScreenCompose(
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val attributes by viewModel.attributes.collectAsState()
    HomeScreenContent(attributes = attributes, actions = viewModel)
}

@Composable
private fun HomeScreenContent(
    attributes: HomeScreenAttributes,
    actions: HomeScreenActions
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        val historyUIItems = attributes.searchHistory.map { entry ->
            SearchItemUIModel(
                id = entry.keyword,
                keyword = entry.keyword,
                date = entry.searchedAt.toString(),
                imageUrl = entry.thumbnailUrl
            )
        }

        SearchFieldCompose(
            modifier = Modifier.padding(vertical = 12.dp),
            query = attributes.query,
            onQueryChange = actions::onQueryChange,
            onSearch = actions::onSearch,
            active = attributes.hasActiveSearch.not() && attributes.query.isNotEmpty(),
            historyItems = historyUIItems,
            onDeleteHistoryItem = { uiModel ->
                actions.onDeleteSearchHistoryItem(uiModel.keyword)
            }
        )

        if (!attributes.hasActiveSearch) {
            if (historyUIItems.isNotEmpty()) {
                RecentSearchesHorizontalCompose(
                    title = "Búsquedas recientes",
                    items = historyUIItems,
                    onItemClick = actions::onSearchHistoryItemClick,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        } else {
            ProductResultsGrid(
                keyword = attributes.query,
                attributes = attributes,
                onFirstResultLoaded = actions::onFirstResultLoaded,
                onProductClick = actions::onProductClick
            )
        }
    }
}

@Composable
private fun ProductResultsGrid(
    keyword: String,
    attributes: HomeScreenAttributes,
    onProductClick: (String) -> Unit,
    onFirstResultLoaded: (String, String?) -> Unit
) {
    val pagingItems = attributes.products.collectAsLazyPagingItems()

    LaunchedEffect(keyword, pagingItems.loadState.refresh) {
        val refreshState = pagingItems.loadState.refresh
        if (refreshState is LoadState.NotLoading && pagingItems.itemCount > 0) {
            val firstProduct = pagingItems[0]
            if (firstProduct != null) {
                onFirstResultLoaded(keyword, firstProduct.thumbnailUrl)
            }
        }
    }

    when {
        pagingItems.loadState.refresh is LoadState.Loading -> {
            LoadingScreenCompose()
        }

        pagingItems.loadState.refresh is LoadState.Error -> {
            println("Error pager: ${(pagingItems.loadState.refresh as LoadState.Error).error.message}")
            ErrorScreenCompose(
                message = "No se pudo conectar al servidor. Verifica tu conexión a internet.",
                onRetry = { pagingItems.retry() }
            )
        }

        pagingItems.itemCount == 0 -> {
            Text(
                text = "No se encontraron productos",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 32.dp)
            )
        }

        else -> {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    count = pagingItems.itemCount,
                    key = { index ->
                        val product = pagingItems[index]
                        if (product != null) "${product.id}_$index" else index
                    }
                ) { index ->
                    val product = pagingItems[index] ?: return@items
                    ProductCardCompose(
                        title = product.title,
                        price = product.price,
                        wasPrice = product.wasPrice,
                        thumbnailUrl = product.thumbnailUrl,
                        isOutOfStock = product.isOutOfStock,
                        onClick = { onProductClick(product.id) }
                    )
                }

                if (pagingItems.loadState.append is LoadState.Loading) {
                    item(span = { GridItemSpan(2) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}