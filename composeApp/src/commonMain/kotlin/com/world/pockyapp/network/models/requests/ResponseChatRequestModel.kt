package com.world.pockyapp.network.models.requests

import kotlinx.serialization.Serializable

@Serializable
data class ResponseChatRequestModel(
    val id: String,
    val senderID: String,
    val status: Boolean,
)
