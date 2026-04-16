package com.app.aerobook.data.repository

import com.app.aerobook.data.api.LocationApi
import com.app.aerobook.data.mapper.LocationMapper
import com.app.aerobook.domain.dispatchers.DispatcherProvider
import com.app.aerobook.domain.model.LocationDetail
import com.app.aerobook.domain.repository.MapRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MapRepositoryImpl @Inject constructor(
    private val apiService: LocationApi,
    private val dispatcherProvider: DispatcherProvider
) : MapRepository {

    private val locationCache = mutableListOf<LocationDetail>()

    private val cachedLocations = mutableListOf<LocationDetail>()


    override suspend fun saveToCache(location: LocationDetail) {
        // Only add if it doesn't already exist (3rd decimal precision)
        val exists = cachedLocations.any {
            isSameLocation(it.latitude, it.longitude, location.latitude, location.longitude)
        }
        if (!exists) {
            cachedLocations.add(0, location) // Add to top (Recent first)
        }
    }

    override suspend fun getLocationInfo(lat: Double, lng: Double): LocationDetail =
        withContext(dispatcherProvider.io) {
            try {
                // 1. Check Cache First (3rd decimal place logic)
                val cached = locationCache.find {
                    isSameLocation(it.latitude, it.longitude, lat, lng)
                }
                if (cached != null) return@withContext cached

                // 2. Parallel API Calls (Geocoding + AQI)
                val aqiDeferred = async { apiService.getAirQuality(lat, lng) }
                val geoCodingDeferred = async { apiService.getReverseGeocode(lat, lng) }

                val aqiResponse = aqiDeferred.await()
                val geocodingResponse = geoCodingDeferred.await()

                // 3. Map to Domain and Cache
                val domainModel =
                    LocationMapper.mapToDomain(geocodingResponse, aqiResponse, lat, lng)
                locationCache.add(domainModel)

                return@withContext domainModel
            } catch (e: Exception) {
                throw e
            }
        }

    override suspend fun getCachedLocations(): List<LocationDetail> {
        return locationCache.toList()
    }

    // --- Helper Logic for Assignment Requirements ---

    private fun isSameLocation(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Boolean {
        val precision = 1000.0 // 3 decimal places
        return Math.round(lat1 * precision) == Math.round(lat2 * precision) &&
                Math.round(lon1 * precision) == Math.round(lon2 * precision)
    }
}