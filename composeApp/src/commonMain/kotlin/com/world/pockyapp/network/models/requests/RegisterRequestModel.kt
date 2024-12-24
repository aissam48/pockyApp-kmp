package com.world.pockyapp.network.models.requests

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestModel(
    val firstName: String,
    val lastName: String,
    val phone: String,
    val email: String,
    val password: String,
    val country: String,
    val city: String,
)
