package com.app.aerobook.di

import com.app.aerobook.domain.dispatchers.DefaultDispatcherProvider
import com.app.aerobook.domain.dispatchers.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDispatcher(): DispatcherProvider = DefaultDispatcherProvider()
}