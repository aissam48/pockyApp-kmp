package com.world.pockyapp.network.models.requests

import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordRequestModel(
    val currentPassword: String,
    val newPassword: String,
)
