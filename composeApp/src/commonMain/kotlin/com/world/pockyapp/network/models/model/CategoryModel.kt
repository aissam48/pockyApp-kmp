package com.world.pockyapp.network.models.model

import kotlinx.serialization.Serializable

@Serializable
data class CategoryModel(
    var id: String,
    val name: String,
)