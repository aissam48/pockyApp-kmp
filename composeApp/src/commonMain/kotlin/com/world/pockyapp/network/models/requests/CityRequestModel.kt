package com.world.pockyapp.network.models.requests

import kotlinx.serialization.Serializable

@Serializable
data class CityRequestModel(
    val country: String,
)
