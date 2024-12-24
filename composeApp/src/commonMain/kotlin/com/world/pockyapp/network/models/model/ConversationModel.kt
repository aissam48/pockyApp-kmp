package com.world.pockyapp.network.models.model

import kotlinx.serialization.Serializable

@Serializable
data class ConversationModel(
    val id: String="",
    val members: List<String> = emptyList(),
    val createdAt: String = "",
    val expired: Boolean = false,
    val profile: ProfileModel = ProfileModel(),
    val lastMessage: MessageModel? = MessageModel()
    )