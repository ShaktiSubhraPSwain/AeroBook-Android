package com.app.aerobook.data.repository

import com.app.aerobook.domain.dispatchers.DispatcherProvider
import kotlinx.coroutines.withContext

abstract class BaseRepository(private val dispatcherProvider: DispatcherProvider) {

    protected suspend fun <T> networkCall(call: suspend () -> T): T {
        return withContext(dispatcherProvider.io) {
            call()
        }
    }
}