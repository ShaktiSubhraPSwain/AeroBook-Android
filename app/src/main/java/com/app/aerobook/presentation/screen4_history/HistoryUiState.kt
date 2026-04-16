package com.app.aerobook.presentation.screen4_history

import com.app.aerobook.domain.model.BookingResult

data class HistoryUiState(
    val bookings: List<BookingResult> = emptyList(),
    val isLoading: Boolean = false,
    val totalPrice: Double? = null,
    val error: String? = null
)