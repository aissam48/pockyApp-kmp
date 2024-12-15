package com.world.pockyapp.network.models.requests

import kotlinx.serialization.Serializable

@Serializable
data class RequestProductModel(
    val productId: String,
    val storeId: String,
    val name: String,
    val phone: String,
    val city: String,
    val address: String,
    val quantity: Int,
    val ownerId: String,
)
