package com.world.pockyapp.network.models.model

import kotlinx.serialization.Serializable

@Serializable
data class DataModel(
    val country: String,
    val cities :List<String>
)