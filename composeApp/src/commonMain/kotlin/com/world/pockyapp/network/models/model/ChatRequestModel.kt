package com.world.pockyapp.network.models.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatRequestModel(
    val id: String,
    val senderID: String,
    val members: List<String> = emptyList(),
    val createdAt: String,
    val expired: Boolean,
    val status: String,
    val sendProfile: ProfileModel = ProfileModel()
    )