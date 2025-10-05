package com.world.pockyapp.network.localDB

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import com.world.pockyapp.network.models.model.MomentModel
import com.world.pockyapp.network.models.model.ChatRequestModel
import com.world.pockyapp.network.models.model.FriendRequestModel

object Converters {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    // --- For List<MomentModel> ---
    @TypeConverter
    fun fromMomentList(value: List<MomentModel>?): String =
        json.encodeToString(value ?: emptyList())

    @TypeConverter
    fun toMomentList(value: String): List<MomentModel> =
        json.decodeFromString(value)

    // --- For ChatRequestModel ---
    @TypeConverter
    fun fromChatRequest(value: ChatRequestModel?): String? =
        value?.let { json.encodeToString(it) }

    @TypeConverter
    fun toChatRequest(value: String?): ChatRequestModel? =
        value?.let { json.decodeFromString(it) }

    // --- For FriendRequestModel ---
    @TypeConverter
    fun fromFriendRequest(value: FriendRequestModel?): String? =
        value?.let { json.encodeToString(it) }

    @TypeConverter
    fun toFriendRequest(value: String?): FriendRequestModel? =
        value?.let { json.decodeFromString(it) }
}
