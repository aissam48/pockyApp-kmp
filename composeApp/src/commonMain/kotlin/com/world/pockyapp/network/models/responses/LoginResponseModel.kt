package com.world.pockyapp.network.models.responses

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseModel(
    val data: LoginResponseData,
)


@Serializable
data class LoginResponseData(
    val message: String,
    val accessToken:String
)