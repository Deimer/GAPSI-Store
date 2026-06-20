package com.deymervilla.domain.usecase.product

import com.deymervilla.domain.repositories.product.ProductRepository
import javax.inject.Inject

class SearchProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {

    operator fun invoke(
        keyword: String
    ) = productRepository.searchProductsByKeyword(
        keyword
    )
}