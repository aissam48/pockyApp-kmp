package com.world.pockyapp.network.models.requests

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestModel(
    val email: String,
    val password: String,
)
