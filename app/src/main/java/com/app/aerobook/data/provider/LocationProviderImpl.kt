package com.app.aerobook.data.provider

import android.annotation.SuppressLint
import com.app.aerobook.domain.provider.LocationProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class LocationProviderImpl @Inject constructor(
    private val client: FusedLocationProviderClient
) : LocationProvider {
    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): LatLng? = suspendCancellableCoroutine { cont ->
        client.lastLocation.addOnSuccessListener { location ->
            cont.resume(location?.let { LatLng(it.latitude, it.longitude) })
        }.addOnFailureListener {
            cont.resume(null)
        }
    }
}