package com.app.aerobook.data.api.interceptor

import com.app.aerobook.data.model.BookingRequest
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.Buffer

class MockInterceptor : Interceptor {
    private val jsonMediaType = "application/json".toMediaTypeOrNull()
    private val endPointPrefix = "/books"
    private val gson = Gson()

    // Companion object makes this list "Global" for the app session
    companion object {
        private val bookingHistory = mutableListOf<String>()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath
        val method = request.method

        return if (path.contains(endPointPrefix)) {
            when (method) {
                "POST" -> {
                    val bodyString = requestBodyToString(request.body)
                    val requestData = gson.fromJson(bodyString, BookingRequest::class.java)

                    // Create a response that includes the locations + a generated price
                    val successResponse = gson.toJson(
                        mapOf(
                            "a" to requestData.a,
                            "b" to requestData.b,
                            "price" to (100..500).random() // Mocked dynamic price
                        )
                    )

                    bookingHistory.add(0, successResponse) // Add new booking to the top

                    createMockResponse(request, successResponse)
                }

                // GET: Return all bookings
                "GET" -> {
                    val historyJson = "[${bookingHistory.joinToString(",")}]"
                    createMockResponse(request, historyJson)
                }

                else -> chain.proceed(request)
            }
        } else {
            chain.proceed(request)
        }
    }

    private fun createMockResponse(request: Request, json: String): Response {
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(ResponseBody.create(jsonMediaType, json))
            .addHeader("content-type", "application/json")
            .build()
    }

    // Helper to read the JSON sent by Retrofit
    private fun requestBodyToString(requestBody: RequestBody?): String {
        return try {
            val buffer = Buffer()
            requestBody?.writeTo(buffer)
            buffer.readUtf8()
        } catch (e: Exception) {
            ""
        }
    }
}