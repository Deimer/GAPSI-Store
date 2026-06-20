package com.deymervilla.domain.di

import com.deymervilla.domain.repositories.product.ProductRepository
import com.deymervilla.domain.repositories.product.ProductRepositoryImpl
import com.deymervilla.domain.repositories.searchhistory.SearchHistoryRepository
import com.deymervilla.domain.repositories.searchhistory.SearchHistoryRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindProductRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    abstract fun bindSearchHistoryRepository(
        searchHistoryRepositoryImpl: SearchHistoryRepositoryImpl
    ): SearchHistoryRepository
}