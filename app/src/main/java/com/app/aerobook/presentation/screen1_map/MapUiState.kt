package com.app.aerobook.presentation.screen1_map

import com.app.aerobook.domain.model.LocationDetail

data class MapUiState(
    val currentMarkerLocation: LocationDetail? = null,
    val locationA: LocationDetail? = null,
    val locationB: LocationDetail? = null,
    val selectionStep: SelectionStep = SelectionStep.SET_A,
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class SelectionStep {
    SET_A, SET_B, BOOK, BOOKING
}