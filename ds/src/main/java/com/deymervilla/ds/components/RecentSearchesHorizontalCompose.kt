package com.deymervilla.ds.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.deymervilla.ds.theme.GAPSIStoreTheme
import com.deymervilla.ds.uimodel.SearchItemUIModel

@Composable
fun RecentSearchesHorizontalCompose(
    title: String,
    items: List<SearchItemUIModel>,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(end = 16.dp)
        ) {
            items(items) { item ->
                RecentSearchCircleItem(
                    item = item,
                    onClick = { onItemClick(item.keyword) }
                )
            }
        }
    }
}

@Composable
private fun RecentSearchCircleItem(
    item: SearchItemUIModel,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(88.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = item.keyword,
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
        Text(
            text = item.keyword,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RecentSearchesHorizontalPreview() {
    GAPSIStoreTheme {
        RecentSearchesHorizontalCompose(
            title = "Búsquedas recientes",
            items = listOf(
                SearchItemUIModel("1", "Inversions", "10:00 AM", "https://via.placeholder.com/150"),
                SearchItemUIModel("2", "Quick Yoga", "11:00 AM", "https://via.placeholder.com/150"),
                SearchItemUIModel("3", "Stretching", "12:00 PM", "https://via.placeholder.com/150")
            ),
            onItemClick = {}
        )
    }
}