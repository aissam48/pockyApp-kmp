package com.world.pockyapp.network.models.requests

import kotlinx.serialization.Serializable

@Serializable
data class LocationRequestModel(
    val country: String,
    val city: String,
)
