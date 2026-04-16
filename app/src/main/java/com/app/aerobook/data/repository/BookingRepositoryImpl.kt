package com.app.aerobook.data.repository

import com.app.aerobook.data.api.BookingApi
import com.app.aerobook.data.mapper.LocationMapper
import com.app.aerobook.domain.dispatchers.DispatcherProvider
import com.app.aerobook.domain.model.BookingResult
import com.app.aerobook.domain.model.LocationDetail
import com.app.aerobook.domain.repository.BookingRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BookingRepositoryImpl @Inject constructor(
    private val apiService: BookingApi,
    private val dispatcherProvider: DispatcherProvider
) : BookingRepository, BaseRepository(dispatcherProvider) {

    override suspend fun createBooking(a: LocationDetail, b: LocationDetail): BookingResult =
        withContext(dispatcherProvider.io) {
            try {
                // Simulate network delay
                delay(1000)

                LocationMapper.mapToBookingResult(
                    apiService.postBooking(
                        LocationMapper.mapToBookingRequest(
                            a, b
                        )
                    )
                )
            } catch (e: Exception) {
                throw e
            }
        }

    override suspend fun getBookingHistory(year: Int, month: Int): List<BookingResult> =
        withContext(dispatcherProvider.io) {
            // In a real app, you'd filter by the year/month parameters here
            apiService.getBookingHistory(year, month).map {
                LocationMapper.mapToBookingResult(it)
            }
        }
}