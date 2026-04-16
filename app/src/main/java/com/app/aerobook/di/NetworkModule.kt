package com.app.aerobook.di

import com.app.aerobook.data.api.LocationApi
import com.app.aerobook.data.api.interceptor.MockInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import com.app.aerobook.BuildConfig
import com.app.aerobook.data.api.BookingApi

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // 1. Base URL - This is used for real APIs (AQI/Geocoding)
    // The MockInterceptor will handle the /books path specifically.
    private const val BASE_URL = "https://api.bigdatacloud.net/data/"

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideMockInterceptor(): MockInterceptor {
        return MockInterceptor()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        mockInterceptor: MockInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor) // Logs real network traffic
            .addInterceptor(mockInterceptor) // Bypasses network for /books
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val originalUrl = originalRequest.url

                // Only add the token if the request is going to the AQI API
                val newUrl = if (originalUrl.host.contains("waqi.info")) {
                    originalUrl.newBuilder()
                        .addQueryParameter("token", BuildConfig.AQI_TOKEN)
                        .build()
                } else {
                    originalUrl
                }

                val requestBuilder = originalRequest.newBuilder().url(newUrl)
                chain.proceed(requestBuilder.build())
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideLocationApi(retrofit: Retrofit): LocationApi {
        return retrofit.create(LocationApi::class.java)
    }

    @Provides
    @Singleton
    fun provideBookingApi(retrofit: Retrofit): BookingApi {
        return retrofit.create(BookingApi::class.java)
    }
}