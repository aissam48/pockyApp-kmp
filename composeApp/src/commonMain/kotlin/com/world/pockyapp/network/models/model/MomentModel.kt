package com.world.pockyapp.network.models.model

import kotlinx.serialization.Serializable

@Serializable
data class MomentModel(
    val id: String = "",
    val ownerID: String = "",
    val mediaUrl: String = "",
    val mimetype: String = "",
    val createdAt: String = "",
    val profile: ProfileModel = ProfileModel(),
    val likes: MutableList<String> = mutableListOf(),
    var viewed: Boolean = false,
    var liked: Boolean = false,
    val views: MutableList<String> = mutableListOf(),
    val geoLocation: GeoLocationModel = GeoLocationModel(),
) {
}