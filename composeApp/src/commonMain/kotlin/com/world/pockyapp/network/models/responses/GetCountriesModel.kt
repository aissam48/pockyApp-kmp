package com.world.pockyapp.network.models.responses

import com.world.pockyapp.network.models.model.DataModel
import kotlinx.serialization.Serializable

@Serializable
data class GetCountriesModel(
    val error:Boolean,
    val msg:String,
    val data: List<DataModel>,
)

