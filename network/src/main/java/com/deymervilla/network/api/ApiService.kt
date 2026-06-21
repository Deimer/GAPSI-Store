package com.deymervilla.network.api

import com.deymervilla.network.constants.NetworkConstants.DEFAULTS.SORT_BY_BEST_MATCH
import com.deymervilla.network.constants.NetworkConstants.PARAMETERS.PARAMETER_KEYWORD
import com.deymervilla.network.constants.NetworkConstants.PARAMETERS.PARAMETER_PAGE
import com.deymervilla.network.constants.NetworkConstants.PARAMETERS.PARAMETER_SORT_BY
import com.deymervilla.network.constants.NetworkConstants.URLs.SEARCH_BY_KEYWORD_PATH
import com.deymervilla.network.dto.response.SearchResponseDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET(SEARCH_BY_KEYWORD_PATH)
    suspend fun searchProductsByKeyword(
        @Query(PARAMETER_KEYWORD) keyword: String,
        @Query(PARAMETER_PAGE) page: Int,
        @Query(PARAMETER_SORT_BY) sortBy: String = SORT_BY_BEST_MATCH
    ): SearchResponseDTO
}