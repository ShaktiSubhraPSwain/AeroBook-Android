package com.app.aerobook.domain.repository

import com.app.aerobook.domain.model.BookingResult
import com.app.aerobook.domain.model.LocationDetail

interface BookingRepository {
    suspend fun createBooking(a: LocationDetail, b: LocationDetail): BookingResult
    suspend fun getBookingHistory(year: Int, month: Int): List<BookingResult>
}