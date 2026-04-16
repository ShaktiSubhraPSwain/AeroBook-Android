package com.app.aerobook.domain.model

data class BookingResult(
    val a: LocationDetail,
    val b: LocationDetail,
    val price: Double,
    val id: String?= null,
    val timestamp: Long?= null,
)