package com.deymervilla.domain.usecase.searchhistory

import com.deymervilla.domain.repositories.searchhistory.SearchHistoryRepository
import javax.inject.Inject

class ClearSearchHistoryUseCase @Inject constructor(
    private val searchHistoryRepository: SearchHistoryRepository
) {

    suspend operator fun invoke() =
        searchHistoryRepository.clearSearchHistory()
}