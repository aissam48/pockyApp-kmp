package com.world.pockyapp.network.models.model

import kotlinx.serialization.Serializable

@Serializable
data class ErrorModel(
    val code: Int = 200,
    val message: String = ""
)