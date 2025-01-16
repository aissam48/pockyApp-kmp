package com.world.pockyapp.network.models.model

import kotlinx.serialization.Serializable

@Serializable
data class StreetModel(
    val geoLocation: GeoLocationModel = GeoLocationModel(),
    val moments: MutableList<MomentModel> = mutableListOf(),
)