package com.app.aerobook.data.model

import com.google.gson.annotations.SerializedName

data class BookingRequest(
    @SerializedName("a") val a: LocationDto,
    @SerializedName("b") val b: LocationDto
)

data class BookingResponse(
    @SerializedName("a") val a: LocationDto,
    @SerializedName("b") val b: LocationDto,
    @SerializedName("price") val price: Double
)

data class LocationDto(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("aqi") val aqi: Int,
    @SerializedName("address") val address: String,
    @SerializedName("nickname") val nickname: String? = null,
    @SerializedName("id") val id: String
)