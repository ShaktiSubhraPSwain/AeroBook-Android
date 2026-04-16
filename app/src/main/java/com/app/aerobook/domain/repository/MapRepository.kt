package com.app.aerobook.domain.repository

import com.app.aerobook.domain.model.LocationDetail

interface MapRepository {
    suspend fun getLocationInfo(lat: Double, lng: Double): LocationDetail

    suspend fun getCachedLocations(): List<LocationDetail>

    suspend fun saveToCache(location: LocationDetail)
}