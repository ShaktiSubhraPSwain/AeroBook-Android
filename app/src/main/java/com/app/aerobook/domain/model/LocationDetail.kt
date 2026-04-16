package com.app.aerobook.domain.model

data class LocationDetail(
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val aqi: Int,
    val nickname: String? = null,
    val id: String
) {
    // Helper to decide whether to show the nickname or the raw address
    val displayName: String
        get() = if (!nickname.isNullOrBlank()) nickname else address
}