package com.world.pockyapp.network.models.responses

import com.world.pockyapp.network.models.model.PostModel
import com.world.pockyapp.network.models.model.ProfileModel
import kotlinx.serialization.Serializable

@Serializable
data class SearchResponseModel(
    val message: String,
    val users: List<ProfileModel>
)

