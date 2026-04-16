package com.app.aerobook.data.api

import com.app.aerobook.data.model.BookingRequest
import com.app.aerobook.data.model.BookingResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface BookingApi {

    @POST("books")
    suspend fun postBooking(
        @Body request: BookingRequest
    ): BookingResponse

    @GET("books")
    suspend fun getBookingHistory(
        @Query("year") year: Int,
        @Query("month") month: Int
    ): List<BookingResponse>

}