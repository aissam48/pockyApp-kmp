package com.world.pockyapp.network.models.responses

import com.world.pockyapp.network.models.model.ProfileModel
import kotlinx.serialization.Serializable

@Serializable
data class GetProfileResponseModel(
    val message: String,
    val profile: ProfileModel
)

