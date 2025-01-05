package com.world.pockyapp.network.models.responses

import com.world.pockyapp.network.models.model.ProfileModel
import kotlinx.serialization.Serializable

@Serializable
data class GetFriendsMomentsResponseModel(
    val message: String,
    val moments: List<ProfileModel>
)

