package com.deymervilla.ds.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.deymervilla.ds.theme.GAPSIStoreTheme
import com.deymervilla.ds.uimodel.SearchItemUIModel

@Composable
fun SearchFieldCompose(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    active: Boolean,
    historyItems: List<SearchItemUIModel>,
    onDeleteHistoryItem: (SearchItemUIModel) -> Unit = {},
    placeholder: String = "Buscar productos..."
) {
    val containerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f)
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        TextField(
            value = query,
            onValueChange = {
                if (it.length <= 50) {
                    onQueryChange(it)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp),
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Limpiar"
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = containerColor,
                unfocusedContainerColor = containerColor,
                disabledContainerColor = containerColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search,
                capitalization = KeyboardCapitalization.Sentences
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearch()
                    keyboardController?.hide()
                }
            ),
            textStyle = MaterialTheme.typography.titleMedium
        )
        AnimatedVisibility(
            visible = active && historyItems.isNotEmpty(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(16.dp),
                color = containerColor,
                tonalElevation = 1.dp
            ) {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 600.dp)
                ) {
                    items(historyItems) { item ->
                        SearchHistoryItemCompose(
                            item = item,
                            onClick = {
                                onQueryChange(item.keyword)
                                onSearch()
                            },
                            onDelete = { onDeleteHistoryItem(item) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchFieldComposePreview() {
    GAPSIStoreTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            SearchFieldCompose(
                query = "sony",
                onQueryChange = {},
                onSearch = {},
                active = true,
                historyItems = listOf(
                    SearchItemUIModel("1", "Samsung Galaxy", "8:22 AM", ""),
                    SearchItemUIModel("2", "Sony PlayStation 5", "Ayer", "")
                )
            )
        }
    }
}