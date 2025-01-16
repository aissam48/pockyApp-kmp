package com.world.pockyapp.network.models.model

import kotlinx.serialization.Serializable

@Serializable
data class GeoLocationModel(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var street: String = "",
    var country: String = "",
    var postalCode: String = "",
    var name: String = ""
)