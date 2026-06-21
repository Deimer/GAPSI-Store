package com.deymervilla.datasource.di

import com.deymervilla.datasource.local.product.ProductLocalDataSource
import com.deymervilla.datasource.local.product.ProductLocalDataSourceImpl
import com.deymervilla.datasource.local.searchhistory.SearchHistoryLocalDataSource
import com.deymervilla.datasource.local.searchhistory.SearchHistoryLocalDataSourceImpl
import com.deymervilla.datasource.remote.product.ProductRemoteDataSource
import com.deymervilla.datasource.remote.product.ProductRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class DataSourceModule {

    @Binds
    abstract fun bindProductRemoteDataSource(
        remoteDataSourceImpl: ProductRemoteDataSourceImpl
    ): ProductRemoteDataSource

    @Binds
    abstract fun bindProductLocalDataSource(
        localDataSourceImpl: ProductLocalDataSourceImpl
    ): ProductLocalDataSource

    @Binds
    abstract fun bindSearchHistoryLocalDataSource(
        localDataSourceImpl: SearchHistoryLocalDataSourceImpl
    ): SearchHistoryLocalDataSource
}