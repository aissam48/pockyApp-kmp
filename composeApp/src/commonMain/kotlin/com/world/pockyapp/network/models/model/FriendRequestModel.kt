package com.world.pockyapp.network.models.model

import kotlinx.serialization.Serializable

@Serializable
data class FriendRequestModel(
    val id: String = "",
    val senderID: String = "",
    val members: List<String> = emptyList(),
    val createdAt: String = "",
    val expired: Boolean = false,
    val status: String = "",
    val profile: ProfileModel = ProfileModel()
)