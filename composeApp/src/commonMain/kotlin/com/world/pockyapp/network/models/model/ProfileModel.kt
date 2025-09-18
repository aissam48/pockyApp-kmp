package com.world.pockyapp.network.models.model

import kotlinx.serialization.Serializable

@Serializable
data class ProfileModel(
    var id: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var username: String = "",
    var phone: String = "",
    var email: String = "",
    var country: String = "",
    var city: String = "",
    var description: String = "",
    val photoID: String = "",
    val isPhoneNumberVerified: Boolean = false,
    val isEmailVerified: Boolean = false,
    val moments: List<MomentModel> = emptyList(),
    val chatRequest: ChatRequestModel? = null,
    val conversationID: String = "",
    val friend: String = "NO",
    val block: String = "NO",
    val follower: Boolean = false,
    val following: Boolean = false,
    val followers: Int = 0,
    val followings: Int = 0,
    val friends: Int = 0,
    val momentsNumber: Int = 0,
)