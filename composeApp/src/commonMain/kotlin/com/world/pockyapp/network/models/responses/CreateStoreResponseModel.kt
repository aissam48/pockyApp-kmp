package com.world.pockyapp.network.models.responses

import com.world.pockyapp.network.models.model.StoreModel
import kotlinx.serialization.Serializable

@Serializable
data class CreateStoreResponseModel(
    val message: String,
    val data: StoreModel,
)

