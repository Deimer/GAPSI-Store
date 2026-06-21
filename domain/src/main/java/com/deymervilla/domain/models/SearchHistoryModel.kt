package com.deymervilla.domain.models

data class SearchHistoryModel(
    val keyword: String,
    val searchedAt: Long,
    val thumbnailUrl: String?
)