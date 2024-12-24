package com.world.pockyapp.network.models.model

import kotlinx.serialization.Serializable

@Serializable
data class MomentModel(
    val postID: String,
    val ownerID: String,
    val mimetype: String,
    val createdAt: String,

    )