package com.app.aerobook.data.api

import com.app.aerobook.BuildConfig
import com.app.aerobook.data.model.AqiResponse
import com.app.aerobook.data.model.BookingRequest
import com.app.aerobook.data.model.BookingResponse
import com.app.aerobook.data.model.GeocodingResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface LocationApi {
    @GET("https://api.bigdatacloud.net/data/reverse-geocode-client")
    suspend fun getReverseGeocode(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("localityLanguage") lang: String = "en"
    ): GeocodingResponse

    @GET("https://api.waqi.info/feed/geo:{lat};{lon}/")
    suspend fun getAirQuality(
        @Path("lat") lat: Double,
        @Path("lon") lon: Double,
        @Query("token") token: String = BuildConfig.AQI_TOKEN
    ): AqiResponse
}