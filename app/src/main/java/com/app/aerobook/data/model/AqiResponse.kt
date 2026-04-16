package com.app.aerobook.data.model

import com.google.gson.annotations.SerializedName

data class AqiResponse(
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: AqiData
)

data class AqiData(
    @SerializedName("aqi") val aqi: Int,
    @SerializedName("city") val city: CityInfo? = null
)

data class CityInfo(
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String
)