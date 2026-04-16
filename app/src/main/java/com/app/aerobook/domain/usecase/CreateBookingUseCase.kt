package com.app.aerobook.domain.usecase

import com.app.aerobook.domain.model.BookingResult
import com.app.aerobook.domain.model.LocationDetail
import com.app.aerobook.domain.repository.BookingRepository
import com.app.aerobook.domain.repository.MapRepository
import javax.inject.Inject

class CreateBookingUseCase @Inject constructor(private val repository: BookingRepository) {

    suspend operator fun invoke(a: LocationDetail, b: LocationDetail): Result<BookingResult> {
        return try {
            val details = repository.createBooking(a, b)
            Result.success(details)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}