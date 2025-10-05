package com.world.pockyapp.network.models.model

import kotlinx.serialization.Serializable

@Serializable
data class PostModel(
    val id: String = "",
    val ownerID: String = "",
    val mediaUrl: String = "",
    val mimetype: String = "",
    val createdAt: String = "",
    val profile: ProfileModel = ProfileModel(),
    val likes: MutableSet<String> = mutableSetOf(),
    var liked: Boolean = false,
    val geoLocation: GeoLocationModel = GeoLocationModel(),
)