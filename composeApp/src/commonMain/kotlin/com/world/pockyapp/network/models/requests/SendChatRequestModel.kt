package com.world.pockyapp.network.models.requests

import kotlinx.serialization.Serializable

@Serializable
data class SendChatRequestModel(
    val otherUserID: String,
)
