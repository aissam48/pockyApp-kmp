package com.world.pockyapp.network.models.requests

import kotlinx.serialization.Serializable

@Serializable
data class ResponseChatRequestModel(
    val id: String,
    val status: Boolean,
)
