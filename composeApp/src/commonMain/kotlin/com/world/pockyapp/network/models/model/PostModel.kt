package com.world.pockyapp.network.models.model

import kotlinx.serialization.Serializable

@Serializable
data class PostModel(
    val postID: String = "",
    val ownerID: String = "",
    val mimetype: String = "",
    val createdAt: String = "",
    val profile: ProfileModel = ProfileModel(),
    val likes: MutableList<String> = mutableListOf(),
    var viewed: Boolean = false,
    val views: MutableList<String> = mutableListOf()
)