package com.app.aerobook.data.mapper

import com.app.aerobook.data.model.AqiResponse
import com.app.aerobook.data.model.BookingRequest
import com.app.aerobook.data.model.BookingResponse
import com.app.aerobook.data.model.GeocodingResponse
import com.app.aerobook.data.model.LocationDto
import com.app.aerobook.domain.model.BookingResult
import com.app.aerobook.domain.model.LocationDetail

object LocationMapper {

    fun mapToDomain(geo: GeocodingResponse, aqiResp: AqiResponse, lat: Double, lon: Double): LocationDetail {
        // Sort by order descending and take first two
        val sortedAdmin = geo.localityInfo?.administrative
            ?.sortedByDescending { it.order }
            ?.take(2)
            ?: emptyList()

        val formattedAddress = sortedAdmin.reversed().joinToString(", ") { it.name }

        return LocationDetail(
            id = "$lat-$lon", // Or use a hash
            address = formattedAddress,
            latitude = lat,
            longitude = lon,
            aqi = aqiResp.data.aqi
        )
    }

    fun mapToDomain(aqiResp: AqiResponse, lat: Double, lon: Double): LocationDetail {

        // Combine names: "Seocho District, Yangjae 2(i)-dong" (if reversed)
        // Note: Usually the highest order is the most specific (Street/Dong),
        // the second highest is the District.

        return LocationDetail(
            id = "$lat-$lon", // Or use a hash
            address = aqiResp.data.city?.name ?: "",
            latitude = lat,
            longitude = lon,
            aqi = aqiResp.data.aqi
        )
    }

    fun mapToDto(domain: LocationDetail): LocationDto {
        return LocationDto(
            domain.latitude,
            domain.longitude,
            domain.aqi,
            domain.address,
            domain.nickname,
            domain.id
        )
    }

    fun mapToBookingRequest(a: LocationDetail, b: LocationDetail): BookingRequest {
        return BookingRequest(
            mapToDto(a),
            mapToDto(b)
        )
    }

    fun mapToBookingResult(response: BookingResponse): BookingResult {
        return BookingResult(
            a = mapDtoToDomain(response.a),
            b = mapDtoToDomain(response.b),
            price = response.price
        )
    }

    private fun mapDtoToDomain(dto: LocationDto): LocationDetail {
        return LocationDetail(
            latitude = dto.latitude,
            longitude = dto.longitude,
            address = dto.address,
            nickname = dto.nickname,
            aqi = dto.aqi,
            id = dto.id
        )
    }
}