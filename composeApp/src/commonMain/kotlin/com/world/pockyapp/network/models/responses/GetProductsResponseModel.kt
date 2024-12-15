package com.world.pockyapp.network.models.responses

import com.world.pockyapp.network.models.model.ProductModel
import kotlinx.serialization.Serializable

@Serializable
data class GetProductsResponseModel(
    val message: String,
    val products: List<ProductModel>
)

