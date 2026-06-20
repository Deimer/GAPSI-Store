package com.deymervilla.domain.usecase.searchhistory

import com.deymervilla.domain.repositories.searchhistory.SearchHistoryRepository
import javax.inject.Inject

class FetchSearchHistoryUseCase @Inject constructor(
    private val searchHistoryRepository: SearchHistoryRepository
) {

    operator fun invoke() =
        searchHistoryRepository.fetchSearchHistory()
}