package com.app.aerobook.data.di

import com.app.aerobook.data.provider.LocationProviderImpl
import com.app.aerobook.data.repository.BookingRepositoryImpl
import com.app.aerobook.data.repository.MapRepositoryImpl
import com.app.aerobook.domain.provider.LocationProvider
import com.app.aerobook.domain.repository.BookingRepository
import com.app.aerobook.domain.repository.MapRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMapRepository(
        mapRepositoryImpl: MapRepositoryImpl
    ): MapRepository

    @Binds
    @Singleton
    abstract fun bindBookingRepository(
        bookingRepository: BookingRepositoryImpl
    ): BookingRepository

    @Binds
    @Singleton
    abstract fun bindLocationProvider(
        locationProviderImpl: LocationProviderImpl
    ): LocationProvider
}