package com.world.pockyapp.network.models.responses

import com.world.pockyapp.network.models.model.ProductModel
import kotlinx.serialization.Serializable

@Serializable
data class ProductsResponseModel(
    val products: List<ProductModel>)

