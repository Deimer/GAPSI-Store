package com.deymervilla.domain.mappers

import com.deymervilla.database.entities.SearchHistoryEntity
import com.deymervilla.domain.models.SearchHistoryModel

fun SearchHistoryEntity.toModel(): SearchHistoryModel = SearchHistoryModel(
    keyword = keyword,
    searchedAt = searchedAt,
    thumbnailUrl = thumbnailUrl
)

fun SearchHistoryModel.toEntity(): SearchHistoryEntity = SearchHistoryEntity(
    keyword = keyword,
    searchedAt = searchedAt,
    thumbnailUrl = thumbnailUrl
)