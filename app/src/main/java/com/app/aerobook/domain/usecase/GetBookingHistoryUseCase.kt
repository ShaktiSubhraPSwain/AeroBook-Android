package com.app.aerobook.domain.usecase

import com.app.aerobook.domain.model.BookingResult
import com.app.aerobook.domain.repository.BookingRepository
import com.app.aerobook.domain.repository.MapRepository
import javax.inject.Inject

class GetBookingHistoryUseCase @Inject constructor(private val repository: BookingRepository) {

    suspend operator fun invoke(year: Int, month: Int): Result<List<BookingResult>> {
        return try {
            val history = repository.getBookingHistory(year, month)
            Result.success(history)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}