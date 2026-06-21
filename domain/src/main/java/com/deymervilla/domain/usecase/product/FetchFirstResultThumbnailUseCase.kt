package com.deymervilla.domain.usecase.product

import com.deymervilla.domain.repositories.product.ProductRepository
import javax.inject.Inject

class FetchFirstResultThumbnailUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {

    suspend operator fun invoke(keyword: String) =
        productRepository.fetchFirstResultThumbnail(keyword)
}