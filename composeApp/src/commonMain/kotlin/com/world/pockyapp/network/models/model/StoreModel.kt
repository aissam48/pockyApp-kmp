package com.world.pockyapp.network.models.model

import com.world.pockyapp.network.models.model.ProductModel
import kotlinx.serialization.Serializable

@Serializable
data class StoreModel(
    val id: String,
    val storeName: String,
    val description: String,
    val phone: String,
    val address: String,
    val email: String,
    val ownerId: String,
    val createdAt: String,
    val status: String,
    val products: List<ProductModel>
)