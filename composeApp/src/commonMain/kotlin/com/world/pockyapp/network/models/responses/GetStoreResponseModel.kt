package com.world.pockyapp.network.models.responses

import com.world.pockyapp.network.models.model.StoreModel
import kotlinx.serialization.Serializable

@Serializable
data class GetStoreResponseModel(
    val store: StoreModel,
)

