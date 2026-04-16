package com.app.aerobook.domain.usecase

import com.app.aerobook.domain.model.LocationDetail
import com.app.aerobook.domain.repository.MapRepository
import javax.inject.Inject

class GetLocationInfoUseCase @Inject constructor(private val repository: MapRepository) {

    suspend operator fun invoke(lat: Double, lng: Double): Result<LocationDetail> {
        return try {
            val details = repository.getLocationInfo(lat, lng)
            Result.success(details)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}