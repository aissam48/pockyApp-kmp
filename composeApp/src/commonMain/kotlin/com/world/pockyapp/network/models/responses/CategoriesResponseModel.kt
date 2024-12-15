package com.world.pockyapp.network.models.responses

import com.world.pockyapp.network.models.model.CategoryModel
import kotlinx.serialization.Serializable

@Serializable
data class CategoriesResponseModel(
    val categories: List<CategoryModel>)

