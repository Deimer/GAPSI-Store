package com.deymervilla.network.di

import com.deymervilla.network.BuildConfig
import com.deymervilla.network.api.ApiService
import com.deymervilla.network.constants.NetworkConstants.DEFAULTS.DEFAULT_TIMEOUT
import com.deymervilla.network.constants.NetworkConstants.HEADERS.RAPID_API_HOST_VALUE
import com.deymervilla.network.constants.NetworkConstants.PARAMETERS.HEADER_RAPID_API_HOST
import com.deymervilla.network.constants.NetworkConstants.PARAMETERS.HEADER_RAPID_API_KEY
import com.deymervilla.network.dto.response.SearchResponseDTO
import com.deymervilla.network.parser.SearchResultDeserializer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    fun provideGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(SearchResponseDTO::class.java, SearchResultDeserializer())
            .create()
    }

    @Provides
    fun provideRapidApiHeaderInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader(HEADER_RAPID_API_KEY, BuildConfig.RAPIDAPI_KEY)
                .addHeader(HEADER_RAPID_API_HOST, RAPID_API_HOST_VALUE)
                .build()
            chain.proceed(request)
        }
    }

    @Provides
    fun provideOkHttpClientApi(
        rapidApiHeaderInterceptor: Interceptor
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .callTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(rapidApiHeaderInterceptor)
            .apply { if (BuildConfig.DEBUG) addInterceptor(logging) }
            .build()
    }

    @Provides
    fun provideRetrofitApi(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    fun provideApi(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}