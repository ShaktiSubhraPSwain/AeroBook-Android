package com.app.aerobook.data.model

import com.google.gson.annotations.SerializedName

data class AddressResponse(
    @SerializedName("localityInfo") val localityInfo: LocalityInfo
)

data class LocalityInfo(
    @SerializedName("administrative") val administrative: List<AdministrativeArea>
)

data class AdministrativeArea(
    @SerializedName("order") val order: Int,
    @SerializedName("name") val name: String,
    @SerializedName("adminLevel") val adminLevel: Int? = null,
    @SerializedName("description") val description: String? = null
)