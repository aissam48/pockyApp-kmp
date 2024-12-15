package com.world.pockyapp.network.models.requests

import com.world.pockyapp.network.models.model.CategoryModel
import kotlinx.serialization.Serializable

@Serializable
data class AddProductRequestModel(
    val productName: String,
    val category: CategoryModel?,
    val description: String,
    val price: Double,
    val stock: Int,
    val isNew: Boolean
)
