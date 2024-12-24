package com.world.pockyapp.network.models.responses

import com.world.pockyapp.network.models.model.PostModel
import kotlinx.serialization.Serializable

@Serializable
data class GetPostsResponseModel(
    val message: String,
    val posts: List<PostModel>
)

