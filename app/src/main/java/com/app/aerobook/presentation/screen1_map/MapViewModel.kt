package com.app.aerobook.presentation.screen1_map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.aerobook.domain.dispatchers.DispatcherProvider
import com.app.aerobook.domain.model.BookingResult
import com.app.aerobook.domain.model.LocationDetail
import com.app.aerobook.domain.provider.LocationProvider
import com.app.aerobook.domain.usecase.CreateBookingUseCase
import com.app.aerobook.domain.usecase.GetLocationInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getLocationInfoUseCase: GetLocationInfoUseCase,
    private val createBookingUseCase: CreateBookingUseCase,
    private val locationProvider: LocationProvider,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent = _navigationEvent
    private val _cachedLocations = MutableStateFlow<List<LocationDetail>>(emptyList())

    private var searchJob: Job? = null

    init {
        fetchUserCurrentLocation()
    }

    fun onCameraIdle(lat: Double, lng: Double) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch(dispatcherProvider.main) {
            delay(500)
            fetchLocationDetails(lat, lng)
        }
    }

    private suspend fun fetchLocationDetails(lat: Double, lng: Double) {
        _uiState.update { it.copy(isLoading = true) }
        getLocationInfoUseCase(lat, lng).fold(
            onSuccess = { detail ->
                _uiState.update {
                    it.copy(
                        currentMarkerLocation = detail,
                        isLoading = false,
                        error = null
                    )
                }
            },
            onFailure = { error ->
                _uiState.update {
                    it.copy(isLoading = false, error = error.message)
                }
            }
        )
    }

    fun performBooking() {
        val locationA = _uiState.value.locationA
        val locationB = _uiState.value.locationB

        if (locationA == null || locationB == null) {
            _uiState.update { it.copy(error = "Please select both locations.") }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, selectionStep = SelectionStep.BOOKING)
            }

            createBookingUseCase(locationA, locationB).fold(
                onSuccess = { details ->
                    _navigationEvent.emit(
                        NavigationEvent.BookingDetailsScreen(details)
                    )
                    resetToInitialState()
                },
                onFailure = { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message ?: "Booking failed. Please try again."
                        )
                    }
                }
            )
        }
    }

    fun onConfirmLocation() {
        val current = _uiState.value.currentMarkerLocation ?: return

        _uiState.update { state ->
            when (state.selectionStep) {
                SelectionStep.SET_A -> state.copy(
                    locationA = current,
                    selectionStep = SelectionStep.SET_B
                )

                SelectionStep.SET_B -> state.copy(
                    locationB = current,
                    selectionStep = SelectionStep.BOOK
                )

                else -> state // Navigation handled in UI
            }
        }
    }

    fun updateNickname(locationId: String, nickname: String) {
        _uiState.update { state ->
            if (state.locationA?.id == locationId) {
                state.copy(locationA = state.locationA.copy(nickname = nickname))
            } else if (state.locationB?.id == locationId) {
                state.copy(locationB = state.locationB.copy(nickname = nickname))
            } else {
                state
            }
        }
    }

    fun fetchUserCurrentLocation() {
        viewModelScope.launch(dispatcherProvider.io) {
            val userLatLng = locationProvider.getCurrentLocation()

            userLatLng?.let { latLng ->
                // 2. Trigger the Use Case to get Address/AQI for this spot
                onCameraIdle(latLng.latitude, latLng.longitude)

                // 3. Update a "Camera Move" event so the UI knows to zoom
                _navigationEvent.emit(
                    NavigationEvent.MoveCameraTo(
                        latLng.latitude,
                        latLng.longitude
                    )
                )
            }
        }
    }

    private fun resetToInitialState() {
        _uiState.update {
            MapUiState() // Assuming MapUiState is your default state with null locations
        }
    }
}

sealed class NavigationEvent {
    data class MoveCameraTo(val lat: Double, val lng: Double) : NavigationEvent()
    data class BookingDetailsScreen(val booking: BookingResult) : NavigationEvent()
}