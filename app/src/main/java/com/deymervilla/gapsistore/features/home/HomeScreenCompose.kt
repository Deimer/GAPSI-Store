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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.deymervilla.domain.models.SearchHistoryModel
import com.deymervilla.ds.components.ProductCardCompose
import com.deymervilla.ds.components.SearchFieldCompose
import com.deymervilla.ds.components.SearchHistoryItemCompose
import com.deymervilla.ds.screens.ErrorScreenCompose
import com.deymervilla.ds.screens.LoadingScreenCompose

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
        SearchFieldCompose(
            query = attributes.query,
            onQueryChange = actions::onQueryChange,
            onSearch = actions::onSearch,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        if (!attributes.hasActiveSearch) {
            SearchHistorySection(
                history = attributes.searchHistory,
                onItemClick = actions::onSearchHistoryItemClick,
                onDeleteItem = actions::onDeleteSearchHistoryItem,
                onClearAll = actions::onClearSearchHistory
            )
        } else {
            ProductResultsGrid(
                attributes = attributes,
                onProductClick = actions::onProductClick
            )
        }
    }
}

@Composable
private fun SearchHistorySection(
    history: List<SearchHistoryModel>,
    onItemClick: (String) -> Unit,
    onDeleteItem: (String) -> Unit,
    onClearAll: () -> Unit
) {
    if (history.isEmpty()) return

    Column {
        Text(
            text = "Búsquedas recientes",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        history.forEach { entry ->
            SearchHistoryItemCompose(
                keyword = entry.keyword,
                thumbnailUrl = entry.thumbnailUrl,
                onClick = { onItemClick(entry.keyword) },
                onDelete = { onDeleteItem(entry.keyword) }
            )
        }
    }
}

@Composable
private fun ProductResultsGrid(
    attributes: HomeScreenAttributes,
    onProductClick: (String) -> Unit
) {
    val pagingItems = attributes.products.collectAsLazyPagingItems()

    when {
        pagingItems.loadState.refresh is LoadState.Loading -> {
            LoadingScreenCompose()
        }

        pagingItems.loadState.refresh is LoadState.Error -> {
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
                    key = pagingItems.itemKey { it.id }
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