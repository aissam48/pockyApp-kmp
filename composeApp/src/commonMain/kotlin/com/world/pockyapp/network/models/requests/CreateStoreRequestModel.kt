package com.world.pockyapp.network.models.requests

import kotlinx.serialization.Serializable

@Serializable
data class CreateStoreRequestModel(
    val storeName: String,
    val description: String,
    val phone: String,
    val address: String,
    val email: String
)
