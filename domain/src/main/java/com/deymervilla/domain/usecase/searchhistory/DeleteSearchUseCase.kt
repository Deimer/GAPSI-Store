package com.deymervilla.domain.usecase.searchhistory

import com.deymervilla.domain.repositories.searchhistory.SearchHistoryRepository
import javax.inject.Inject

class DeleteSearchUseCase @Inject constructor(
    private val searchHistoryRepository: SearchHistoryRepository
) {

    suspend operator fun invoke(
        keyword: String
    ) = searchHistoryRepository.deleteSearch(
        keyword
    )
}