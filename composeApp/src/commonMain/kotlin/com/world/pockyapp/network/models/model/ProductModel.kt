package com.world.pockyapp.network.models.model

import com.world.pockyapp.network.models.model.CategoryModel
import kotlinx.serialization.Serializable

@Serializable
data class ProductModel(
    val id: String,
    val productName: String,
    val category: CategoryModel,
    val description: String,
    val price: Double,
    val stock: Int,
    val storeId: String,
    val ownerId: String,
    val status: String,
    val createdAt: String,

    )