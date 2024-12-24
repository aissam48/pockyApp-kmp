package com.world.pockyapp.network.models.model

import kotlinx.serialization.Serializable

@Serializable
data class MessageModel(
    val id: String = "",
    val senderID: String="",
    val createdAt: String = "",
    val content: String = "",
    val conversationID: String = "",
)