package com.world.pockyapp.network.models.responses

import com.world.pockyapp.network.models.model.PostModel
import kotlinx.serialization.Serializable

@Serializable
data class ResponsePostModel(
    val message: String = "",
    val post: PostModel = PostModel()
)