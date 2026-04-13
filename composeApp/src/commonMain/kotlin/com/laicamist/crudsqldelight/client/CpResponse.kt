package com.laicamist.crudsqldelight.client

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class CpResponse(
    @SerialName("post code") val postCode: String = "",
    val places: List<Place> = emptyList()
)

@Serializable
data class Place(
    @SerialName("place name") val placeName: String = "",
    val state: String = ""
)