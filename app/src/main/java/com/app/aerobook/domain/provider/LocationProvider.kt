package com.app.aerobook.domain.provider

import com.google.android.gms.maps.model.LatLng

interface LocationProvider {
    suspend fun getCurrentLocation(): LatLng?
}