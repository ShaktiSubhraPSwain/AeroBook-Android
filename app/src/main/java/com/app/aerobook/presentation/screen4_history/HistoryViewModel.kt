package com.app.aerobook.presentation.screen4_history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.aerobook.domain.dispatchers.DispatcherProvider
import com.app.aerobook.domain.model.BookingResult
import com.app.aerobook.domain.usecase.GetBookingHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getBookingHistoryUseCase: GetBookingHistoryUseCase,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        fetchHistory()
    }

    private fun fetchHistory() {
        viewModelScope.launch(dispatcherProvider.io) {
            _uiState.update { it.copy(isLoading = true) }
            val currentPeriod = YearMonth.now()
            val year = currentPeriod.year
            val month = currentPeriod.monthValue
            getBookingHistoryUseCase(year, month).fold(
                onSuccess = { list ->
                    _uiState.update {
                        it.copy(
                            bookings = list,
                            isLoading = false,
                            totalPrice = calculateTotalPrice(list)
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
            )
        }
    }

    private fun calculateTotalPrice(list: List<BookingResult>): Double {
        return list.sumOf {
            it.price
        }
    }
}