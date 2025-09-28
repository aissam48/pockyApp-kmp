package com.world.pockyapp.network.models.model

import kotlinx.serialization.Serializable

@Serializable
data class ChallengeModel(
    val id: String = "",
    val ownerID: String = "",
    val mediaUrl: String = "",
    val mimetype: String = "",
    val createdAt: String = "",
    val profile: ProfileModel = ProfileModel(),
    val title: String = "",
    val description: String = "",
    val rules: String = "",
    val category: String = "",
    val difficulty: String = ""
)