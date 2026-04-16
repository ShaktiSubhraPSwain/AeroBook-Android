package com.app.aerobook.presentation.navigation

import com.app.aerobook.domain.model.BookingResult

sealed class Screen(val route: String) {
    // Screen 1: The main map view
    data object Map : Screen("map_screen")

    // Screen 2: Setting a nickname for a specific address
    data object NickName : Screen("nickname_screen/{locationId}") {
        fun createRoute(locationId: String) = "nickname_screen/$locationId"
    }

    // Screen 3: Confirming the booking details
    data object BookingDetail : Screen("booking_detail_screen/{bookingJson}") {
        fun createRoute(bookingResultJson: String) = "booking_detail_screen/$bookingResultJson"
    }

    // Screen 4: Viewing past bookings
    data object History : Screen("history_screen")
}