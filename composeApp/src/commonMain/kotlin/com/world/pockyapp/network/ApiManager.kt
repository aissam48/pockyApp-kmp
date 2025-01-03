package com.world.pockyapp.network

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.world.pockyapp.Constant
import com.world.pockyapp.getPlatform
import com.world.pockyapp.network.models.model.ChatRequestModel
import com.world.pockyapp.network.models.model.ConversationModel
import com.world.pockyapp.network.models.model.DataModel
import com.world.pockyapp.network.models.model.FriendRequestModel
import com.world.pockyapp.network.models.model.MessageModel
import com.world.pockyapp.network.models.model.PostModel
import com.world.pockyapp.network.models.model.ProfileModel
import com.world.pockyapp.network.models.model.ResponseMessageModel
import com.world.pockyapp.network.models.requests.ChangePasswordRequestModel
import com.world.pockyapp.network.models.requests.LocationRequestModel
import com.world.pockyapp.network.models.requests.LoginRequestModel
import com.world.pockyapp.network.models.requests.RegisterRequestModel
import com.world.pockyapp.network.models.requests.ResponseChatRequestModel
import com.world.pockyapp.network.models.requests.SendChatRequestModel
import com.world.pockyapp.network.models.responses.GetCountriesModel
import com.world.pockyapp.network.models.responses.GetFriendsMomentsResponseModel
import com.world.pockyapp.network.models.responses.GetPostsResponseModel
import com.world.pockyapp.network.models.responses.GetProfileResponseModel
import com.world.pockyapp.network.models.responses.LoginResponseModel
import com.world.pockyapp.network.models.responses.ResponsePostModel
import com.world.pockyapp.network.models.responses.SearchResponseModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.onUpload
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.delete
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class ApiManager(val dataStore: DataStore<Preferences>) {

    private val baseUrl = Constant.SHARED_LINK


    private val client = if (getPlatform().name.contains("Android")) {
        HttpClient(CIO) {
            install(WebSockets) {
                maxFrameSize = Long.MAX_VALUE
                pingIntervalMillis = 20_000
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 300_000
                connectTimeoutMillis = 300_000
                socketTimeoutMillis = 300_000
            }

            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    useAlternativeNames = false
                })
            }
        }
    } else {
        HttpClient() {
            install(WebSockets) {
                maxFrameSize = Long.MAX_VALUE
                pingIntervalMillis = 20_000
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 300_000
                connectTimeoutMillis = 300_000
                socketTimeoutMillis = 300_000
            }

            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    useAlternativeNames = false
                })
            }
        }
    }

    private suspend fun getToken(): String {
        val preferences = dataStore.edit { }
        return preferences[stringPreferencesKey("token")].toString()
    }


    suspend fun register(
        firstName: String,
        lastName: String,
        phone: String,
        email: String,
        password: String,
        country: String,
        city: String,
        onSuccess: (LoginResponseModel) -> Unit,
        onFailure: (String) -> Unit
    ) {

        val registerRequestModel = RegisterRequestModel(
            firstName = firstName,
            lastName = lastName,
            phone = phone,
            email = email,
            password = password,
            country = country,
            city = city
        )


        //16/3/2021
        try {
            val response: HttpResponse = client.post("$baseUrl/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(registerRequestModel)
            }

            if (response.status.isSuccess()) {
                val responseBody: LoginResponseModel =
                    response.body() // Extract response body if request is successful
                onSuccess(responseBody)
            } else {
                val errorMessage: ResponseMessageModel = response.body() // Get the error message from the response
                onFailure(errorMessage.message)
            }
        } catch (e: Exception) {

        }

    }

    suspend fun login(
        email: String,
        password: String,
        onSuccess: (LoginResponseModel) -> Unit,
        onFailure: (String) -> Unit
    ) {

        val loginRequestModel = LoginRequestModel(
            email = email,
            password = password
        )

        try {

            val response: HttpResponse = client.post("$baseUrl/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(loginRequestModel)
            }

            if (response.status.isSuccess()) {
                val responseBody: LoginResponseModel =
                    response.body() // Extract response body if request is successful
                onSuccess(responseBody)
            } else {
                val errorMessage: String =
                    response.bodyAsText() // Get the error message from the response
                onFailure(errorMessage)
            }
        } catch (e: Exception) {

        }
    }

    suspend fun setPost(
        byteArray: ByteArray,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {

        val response: HttpResponse = client.submitFormWithBinaryData(
            url = "$baseUrl/operations/setpost",
            formData {
                append("file", byteArray, Headers.build {
                    println("Original size ${byteArray.size} bytes")
                    append(HttpHeaders.ContentType, "image/jpg")
                    append(HttpHeaders.ContentDisposition, "filename=file")
                })
            }) {
            val token = getToken()
            println("token-----> $token")
            headers { append(HttpHeaders.Authorization, "Bearer $token") }
            onUpload { bytesSentTotal, contentLength ->
                println("Sent $bytesSentTotal bytes from $contentLength")
            }
        }

        if (response.status.isSuccess()) {
            val responseBody: String =
                response.body()
            println("success-----> ${response.bodyAsText()}")
            onSuccess(responseBody)
        } else {
            val errorMessage: String =
                response.bodyAsText()
            onFailure(errorMessage)
        }
    }

    suspend fun editProfile(
        firstName: String,
        lastName: String,
        phone: String,
        email: String,
        description: String,
        byteArray: ByteArray?,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val response: HttpResponse = client.submitFormWithBinaryData(
                url = "$baseUrl/operations/editprofile",
                formData {
                    if (byteArray != null) {
                        append("file", byteArray, Headers.build {
                            println("Original size ${byteArray.size} bytes")
                            append(HttpHeaders.ContentType, "image/jpg")
                            append(HttpHeaders.ContentDisposition, "filename=file")
                        })
                    }

                    append("firstName", firstName)
                    append("lastName", lastName)
                    append("phone", phone)
                    append("email", email)
                    append("description", description)
                }) {
                val token = getToken()
                println("token-----> $token")
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
                onUpload { bytesSentTotal, contentLength ->
                    println("Sent $bytesSentTotal bytes from $contentLength")
                }
            }

            if (response.status.isSuccess()) {
                val responseBody: String =
                    response.body()
                println("success-----> ${response.bodyAsText()}")
                onSuccess(responseBody)
            } else {
                val errorMessage: String =
                    response.bodyAsText()
                onFailure(errorMessage)
            }
        } catch (e: Exception) {

        }

    }

    suspend fun editLocation(
        country: String,
        city: String,
        onSuccess: (LocationRequestModel) -> Unit,
        onFailure: (String) -> Unit
    ) {

        val locationRequestModel = LocationRequestModel(country, city)
        try {
            val response: HttpResponse = client.post("$baseUrl/operations/editlocation") {
                val token = getToken()
                contentType(ContentType.Application.Json)
                setBody(locationRequestModel)
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }

            if (response.status.isSuccess()) {
                val responseBody: LocationRequestModel = response.body()
                println("success-----> ${response.bodyAsText()}")
                onSuccess(responseBody)
            } else {
                val errorMessage: String = response.bodyAsText()
                onFailure(errorMessage)
            }
        } catch (e: Exception) {

        }

    }

    suspend fun getMyPosts(
        onSuccess: (List<PostModel>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val response: HttpResponse = client.get("$baseUrl/operations/myposts") {
                contentType(ContentType.Application.Json)
                val token = getToken()

                println("token-----> $token")
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }

            if (response.status.isSuccess()) {
                val responseBody: GetPostsResponseModel =
                    response.body()
                println("success-----> ${response.bodyAsText()}")
                onSuccess(responseBody.posts)
            } else {
                val errorMessage: String =
                    response.bodyAsText()
                onFailure(errorMessage)
            }
        } catch (e: Exception) {

        }

    }

    suspend fun getMyProfile(
        onSuccess: (ProfileModel) -> Unit,
        onFailure: (String) -> Unit
    ) {

        val response: HttpResponse = client.get("$baseUrl/operations/me") {
            contentType(ContentType.Application.Json)
            val token = getToken()

            println("token-----> $token")
            headers { append(HttpHeaders.Authorization, "Bearer $token") }
        }

        if (response.status.isSuccess()) {
            val responseBody: GetProfileResponseModel =
                response.body()
            println("success-----> ${response.bodyAsText()}")
            onSuccess(responseBody.profile)
        } else {
            val errorMessage: String =
                response.bodyAsText()
            onFailure(errorMessage)
        }
    }

    suspend fun getCountriesAndCities(
        onSuccess: (List<DataModel>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val response: HttpResponse =
                client.get("https://countriesnow.space/api/v0.1/countries") {
                    contentType(ContentType.Application.Json)
                }

            if (response.status.isSuccess()) {
                val responseBody: GetCountriesModel =
                    response.body()
                println("success countries ${response.bodyAsText()}")
                onSuccess(responseBody.data)
            } else {
                val errorMessage: String =
                    response.bodyAsText()
                onFailure(errorMessage)
            }
        } catch (e: Exception) {
            println("error countries ${e.message}")

        }

    }

    suspend fun getFriendsMoments(
        onSuccess: (List<ProfileModel>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val response: HttpResponse = client.get("$baseUrl/operations/friendsmoments") {
                contentType(ContentType.Application.Json)
                val token = getToken()

                println("token-----> $token")
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }

            if (response.status.isSuccess()) {
                val responseBody: List<ProfileModel> =
                    response.body()
                println("success-----> ${response.bodyAsText()}")
                onSuccess(responseBody)
            } else {
                val errorMessage: String =
                    response.bodyAsText()
                onFailure(errorMessage)
            }
        } catch (e: Exception) {

        }

    }

    suspend fun getNearbyMoments(
        onSuccess: (List<ProfileModel>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val response: HttpResponse = client.get("$baseUrl/operations/nearbymoments") {
                contentType(ContentType.Application.Json)
                val token = getToken()

                println("token-----> $token")
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }

            if (response.status.isSuccess()) {
                val responseBody: List<ProfileModel> =
                    response.body()
                println("success-----> ${response.bodyAsText()}")
                onSuccess(responseBody)
            } else {
                val errorMessage: String =
                    response.bodyAsText()
                onFailure(errorMessage)
            }
        } catch (e: Exception) {

        }

    }

    suspend fun getNearbyPosts(
        onSuccess: (List<PostModel>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val response: HttpResponse = client.get("$baseUrl/operations/nearbyposts") {
                contentType(ContentType.Application.Json)
                val token = getToken()

                println("token-----> $token")
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }

            if (response.status.isSuccess()) {
                val responseBody: List<PostModel> =
                    response.body()
                println("success-----> ${response.bodyAsText()}")
                onSuccess(responseBody)
            } else {
                val errorMessage: String =
                    response.bodyAsText()
                onFailure(errorMessage)
            }
        } catch (e: Exception) {

        }

    }

    suspend fun search(
        keyword: String,
        onSuccess: (List<ProfileModel>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val response: HttpResponse = client.get("$baseUrl/operations/search") {
                contentType(ContentType.Application.Json)
                val token = getToken()
                parameter("keyword", keyword)
                println("token-----> $token")
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }

            if (response.status.isSuccess()) {
                val responseBody: SearchResponseModel =
                    response.body()
                println("success-----> ${response.bodyAsText()}")
                onSuccess(responseBody.users)
            } else {
                val errorMessage: String =
                    response.bodyAsText()
                onFailure(errorMessage)
            }
        } catch (e: Exception) {

        }

    }

    suspend fun getProfile(
        id: String,
        onSuccess: (ProfileModel) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val response: HttpResponse = client.get("$baseUrl/operations/profile") {
                contentType(ContentType.Application.Json)
                val token = getToken()
                parameter("id", id)
                println("token-----> $token")
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }

            if (response.status.isSuccess()) {
                val responseBody: GetProfileResponseModel =
                    response.body()
                println("success-----> ${response.bodyAsText()}")
                onSuccess(responseBody.profile)
            } else {
                val errorMessage: String =
                    response.bodyAsText()
                onFailure(errorMessage)
            }
        } catch (e: Exception) {

        }

    }

    suspend fun getPosts(
        id: String,
        onSuccess: (List<PostModel>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val response: HttpResponse = client.get("$baseUrl/operations/posts") {
                contentType(ContentType.Application.Json)
                val token = getToken()
                parameter("id", id)
                println("token-----> $token")
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }

            if (response.status.isSuccess()) {
                val responseBody: GetPostsResponseModel =
                    response.body()
                println("success-----> ${response.bodyAsText()}")
                onSuccess(responseBody.posts)
            } else {
                val errorMessage: String =
                    response.bodyAsText()
                onFailure(errorMessage)
            }
        } catch (e: Exception) {

        }

    }

    suspend fun changePassword(
        currentPassword: String,
        newPassword: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {

        val changePasswordRequestModel = ChangePasswordRequestModel(currentPassword, newPassword)
        try {
            val response: HttpResponse = client.post("$baseUrl/operations/changepassword") {
                val token = getToken()
                contentType(ContentType.Application.Json)
                setBody(changePasswordRequestModel)
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }

            if (response.status.isSuccess()) {
                val responseBody: String = response.body()
                println("success-----> ${response.bodyAsText()}")
                onSuccess(responseBody)
            } else {
                val errorMessage: String = response.bodyAsText()
                onFailure(errorMessage)
            }
        } catch (e: Exception) {

        }

    }

    lateinit var ws: DefaultClientWebSocketSession

    init {
        try {
            val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
            scope.launch {
                ws = client.webSocketSession(Constant.ws)
            }
        } catch (e: Exception) {

        }

    }

    suspend fun sendRequestChat(
        otherUserID: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val sendChatRequestModel = SendChatRequestModel(otherUserID)

            val response: HttpResponse = client.post("$baseUrl/operations/sendrequestchat") {
                val token = getToken()
                contentType(ContentType.Application.Json)
                setBody(sendChatRequestModel)
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }

            if (response.status.isSuccess()) {
                val responseBody: String = response.body()
                println("success-----> ${response.bodyAsText()}")
                onSuccess(responseBody)
            } else {
                val errorMessage: String = response.bodyAsText()
                onFailure(errorMessage)
            }
        } catch (e: Exception) {

        }

    }

    suspend fun responseRequestChat(
        id: String,
        status: Boolean,
        senderID:String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val responseChatRequestModel = ResponseChatRequestModel(id,senderID, status)

            val response: HttpResponse = client.post("$baseUrl/operations/responserequestchat") {
                val token = getToken()
                contentType(ContentType.Application.Json)
                setBody(responseChatRequestModel)
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }

            if (response.status.isSuccess()) {
                val responseBody: String = response.body()
                println("success-----> ${response.bodyAsText()}")
                onSuccess(responseBody)
            } else {
                val errorMessage: String = response.bodyAsText()
                onFailure(errorMessage)
            }
        } catch (e: Exception) {

        }


    }

    suspend fun getAllChatRequests(
        onSuccess: (List<ChatRequestModel>) -> Unit,
        onFailure: (String) -> Unit
    ) {

        try {
            val response: HttpResponse = client.get("$baseUrl/operations/getallchatrequests") {
                val token = getToken()
                contentType(ContentType.Application.Json)
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }

            if (response.status.isSuccess()) {
                val responseBody: List<ChatRequestModel> = response.body()
                println("success-----> ${response.bodyAsText()}")
                onSuccess(responseBody)
            } else {
                val errorMessage: String = response.bodyAsText()
                onFailure(errorMessage)
            }
        } catch (e: Exception) {

        }

    }

    suspend fun getMessages(
        conversationID: String,
        onSuccess: (List<MessageModel>) -> Unit,
        onFailure: (String) -> Unit
    ) {

        try {
            val response: HttpResponse = client.get("$baseUrl/operations/messages") {
                val token = getToken()
                contentType(ContentType.Application.Json)
                parameter("conversationID", conversationID)
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }

            if (response.status.isSuccess()) {
                val responseBody: List<MessageModel> = response.body()
                println("success-----> ${response.bodyAsText()}")
                onSuccess(responseBody)
            } else {
                val errorMessage: String = response.bodyAsText()
                onFailure(errorMessage)
            }
        } catch (e: Exception) {

        }

    }

    suspend fun sendMessage(
        data: MessageModel,
        onSuccess: (List<MessageModel>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val jsonMessage = Json.encodeToString<MessageModel>(data)
            CoroutineScope(Dispatchers.IO).launch {
                ws.send(Frame.Text(jsonMessage))
            }
        } catch (e: Exception) {

        }


    }

    suspend fun getChatRequests(
        onSuccess: (List<ChatRequestModel>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val response: HttpResponse = client.get("$baseUrl/operations/chatrequests") {
                val token = getToken()
                contentType(ContentType.Application.Json)
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }

            if (response.status.isSuccess()) {
                val responseBody: List<ChatRequestModel> = response.body()
                println("success-----> ${response.bodyAsText()}")
                onSuccess(responseBody)
            } else {
                val errorMessage: String = response.bodyAsText()
                onFailure(errorMessage)
            }
        } catch (e: Exception) {

        }

    }

    suspend fun getConversations(
        onSuccess: (List<ConversationModel>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val response: HttpResponse = client.get("$baseUrl/operations/conversations") {
                val token = getToken()
                contentType(ContentType.Application.Json)
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }

            if (response.status.isSuccess()) {
                val responseBody: List<ConversationModel> = response.body()
                println("success-----> ${response.bodyAsText()}")
                onSuccess(responseBody)
            } else {
                val errorMessage: String = response.bodyAsText()
                onFailure(errorMessage)
            }
        } catch (e: Exception) {

        }

    }

    suspend fun like(
        postID: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val response: HttpResponse = client.post("$baseUrl/operations/likepost") {
                val token = getToken()
                contentType(ContentType.Application.Json)
                setBody(mapOf("postID" to postID))
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }

            if (response.status.isSuccess()) {
                val responseBody: String = response.body()
                println("success-----> ${response.bodyAsText()}")
                onSuccess(responseBody)
            } else {
                val errorMessage: String = response.bodyAsText()
                onFailure(errorMessage)
            }
        } catch (e: Exception) {

        }

    }

    suspend fun likeMoment(
        momentID: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val response: HttpResponse = client.post("$baseUrl/operations/likemoment") {
                val token = getToken()
                contentType(ContentType.Application.Json)
                setBody(mapOf("momentID" to momentID))
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }

            if (response.status.isSuccess()) {
                val responseBody: String = response.body()
                println("success-----> ${response.bodyAsText()}")
                onSuccess(responseBody)
            } else {
                val errorMessage: String = response.bodyAsText()
                onFailure(errorMessage)
            }
        } catch (e: Exception) {

        }

    }

    suspend fun unLike(
        postID: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val response: HttpResponse = client.put("$baseUrl/operations/unlikepost") {
                val token = getToken()
                contentType(ContentType.Application.Json)
                setBody(mapOf("postID" to postID))
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }

            if (response.status.isSuccess()) {
                val responseBody: String = response.body()
                println("success-----> ${response.bodyAsText()}")
                onSuccess(responseBody)
            } else {
                val errorMessage: String = response.bodyAsText()
                onFailure(errorMessage)
            }
        } catch (e: Exception) {

        }

    }

    suspend fun unLikeMoment(
        momentID: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val response: HttpResponse = client.put("$baseUrl/operations/unlikemoment") {
                val token = getToken()
                contentType(ContentType.Application.Json)
                setBody(mapOf("momentID" to momentID))
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }

            if (response.status.isSuccess()) {
                val responseBody: String = response.body()
                println("success-----> ${response.bodyAsText()}")
                onSuccess(responseBody)
            } else {
                val errorMessage: String = response.bodyAsText()
                onFailure(errorMessage)
            }
        } catch (e: Exception) {

        }

    }

    suspend fun viewMoment(
        momentID: String,
        ownerID: String,
        onSuccess: (ResponseMessageModel) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val response: HttpResponse = client.post("$baseUrl/operations/viewmoment") {
                val token = getToken()
                contentType(ContentType.Application.Json)
                setBody(mapOf("momentID" to momentID, "ownerID" to ownerID))
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }

            if (response.status.isSuccess()) {
                val responseBody: ResponseMessageModel = response.body()
                println("success-----> ${response.bodyAsText()}")
                onSuccess(responseBody)
            } else {
                val errorMessage: String = response.bodyAsText()
                onFailure(errorMessage)
            }
        } catch (e: Exception) {

        }

    }

    suspend fun getPost(
        postID: String,
        onSuccess: (PostModel) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val response: HttpResponse = client.get("$baseUrl/operations/post") {
                val token = getToken()
                contentType(ContentType.Application.Json)
                parameter("postID", postID)
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }

            if (response.status.isSuccess()) {
                val responseBody: ResponsePostModel = response.body()
                println("success-----> ${response.bodyAsText()}")
                onSuccess(responseBody.post)
            } else {
                val errorMessage: String = response.bodyAsText()
                onFailure(errorMessage)
            }
        } catch (e: Exception) {

        }

    }

    suspend fun deletePost(
        postID: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val response: HttpResponse = client.delete("$baseUrl/operations/post") {
                val token = getToken()
                contentType(ContentType.Application.Json)
                parameter("postID", postID)
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }

            if (response.status.isSuccess()) {
                val responseBody: ResponseMessageModel = response.body()
                println("success-----> ${response.bodyAsText()}")
                onSuccess(responseBody.message)
            } else {
                val errorMessage: String = response.bodyAsText()
                onFailure(errorMessage)
            }
        } catch (e: Exception) {

        }

    }

    suspend fun deleteMoment(
        momentID: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val response: HttpResponse = client.delete("$baseUrl/operations/moment") {
                val token = getToken()
                contentType(ContentType.Application.Json)
                parameter("momentID", momentID)
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }

            if (response.status.isSuccess()) {
                val responseBody: ResponseMessageModel = response.body()
                println("success-----> ${response.bodyAsText()}")
                onSuccess(responseBody.message)
            } else {
                val errorMessage: String = response.bodyAsText()
                onFailure(errorMessage)
            }
        } catch (e: Exception) {

        }

    }

    suspend fun beFriend(
        friendID: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val response: HttpResponse = client.post("$baseUrl/operations/befriend") {
                val token = getToken()
                contentType(ContentType.Application.Json)
                setBody(mapOf("friendID" to friendID))
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }

            if (response.status.isSuccess()) {
                val responseBody: ResponseMessageModel = response.body()
                println("success-----> ${response.bodyAsText()}")
                onSuccess(responseBody.message)
            } else {
                val errorMessage: String = response.bodyAsText()
                onFailure(errorMessage)
            }
        } catch (e: Exception) {

        }

    }

    suspend fun unFriend(
        friendID: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val response: HttpResponse = client.delete("$baseUrl/operations/unfriend") {
                val token = getToken()
                contentType(ContentType.Application.Json)
                parameter("friendID", friendID)
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }

            if (response.status.isSuccess()) {
                val responseBody: ResponseMessageModel = response.body()
                println("success-----> ${response.bodyAsText()}")
                onSuccess(responseBody.message)
            } else {
                val errorMessage: String = response.bodyAsText()
                onFailure(errorMessage)
            }
        } catch (e: Exception) {

        }

    }

    suspend fun getFriendRequests(
        onSuccess: (List<FriendRequestModel>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val response: HttpResponse = client.get("$baseUrl/operations/friendrequests") {
                val token = getToken()
                contentType(ContentType.Application.Json)
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }

            if (response.status.isSuccess()) {
                val responseBody: List<FriendRequestModel> = response.body()
                println("success-----> ${response.bodyAsText()}")
                onSuccess(responseBody)
            } else {
                val errorMessage: String = response.bodyAsText()
                onFailure(errorMessage)
            }
        } catch (e: Exception) {

        }

    }

    suspend fun acceptFriendRequest(
        requestID: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val response: HttpResponse = client.put("$baseUrl/operations/acceptfriend") {
                val token = getToken()
                contentType(ContentType.Application.Json)
                parameter("requestId", requestID)
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }

            if (response.status.isSuccess()) {
                val responseBody: ResponseMessageModel = response.body()
                println("success-----> ${response.bodyAsText()}")
                onSuccess(responseBody.message)
            } else {
                val errorMessage: String = response.bodyAsText()
                onFailure(errorMessage)
            }
        } catch (e: Exception) {

        }

    }

    suspend fun rejectFriendRequest(
        requestID: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val response: HttpResponse = client.put("$baseUrl/operations/rejectfriend") {
                val token = getToken()
                contentType(ContentType.Application.Json)
                parameter("requestId", requestID)
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }

            if (response.status.isSuccess()) {
                val responseBody: ResponseMessageModel = response.body()
                println("success-----> ${response.bodyAsText()}")
                onSuccess(responseBody.message)
            } else {
                val errorMessage: String = response.bodyAsText()
                onFailure(errorMessage)
            }
        } catch (e: Exception) {

        }

    }

    suspend fun cancelConversation(
        conversationID: String,
        chatRequestID: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val response: HttpResponse = client.delete("$baseUrl/operations/cancelconversation") {
                val token = getToken()
                contentType(ContentType.Application.Json)
                parameter("chatRequestID", chatRequestID)
                parameter("conversationID", conversationID)
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }

            if (response.status.isSuccess()) {
                val responseBody: ResponseMessageModel = response.body()
                println("success-----> ${response.bodyAsText()}")
                onSuccess(responseBody.message)
            } else {
                val errorMessage: String = response.bodyAsText()
                onFailure(errorMessage)
            }
        } catch (e: Exception) {

        }

    }

    suspend fun deleteAccount(
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val response: HttpResponse = client.delete("$baseUrl/operations/deleteaccount") {
                val token = getToken()
                contentType(ContentType.Application.Json)
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
            }

            if (response.status.isSuccess()) {
                val responseBody: ResponseMessageModel = response.body()
                println("success-----> ${response.bodyAsText()}")
                onSuccess(responseBody.message)
            } else {
                val errorMessage: String = response.bodyAsText()
                onFailure(errorMessage)
            }
        } catch (e: Exception) {

        }

    }

    suspend fun shareMoment(
        byteArray: ByteArray?,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val response: HttpResponse = client.submitFormWithBinaryData(
                url = "$baseUrl/operations/sharemoment",
                formData {
                    if (byteArray != null) {
                        append("file", byteArray, Headers.build {
                            println("Original size ${byteArray.size} bytes")
                            append(HttpHeaders.ContentType, "image/jpg")
                            append(HttpHeaders.ContentDisposition, "filename=file")
                        })
                    }

                }) {
                val token = getToken()
                println("token-----> $token")
                headers { append(HttpHeaders.Authorization, "Bearer $token") }
                onUpload { bytesSentTotal, contentLength ->
                    println("Sent $bytesSentTotal bytes from $contentLength")
                }
            }

            if (response.status.isSuccess()) {
                val responseBody: String =
                    response.body()
                println("success-----> ${response.bodyAsText()}")
                onSuccess(responseBody)
            } else {
                val errorMessage: String =
                    response.bodyAsText()
                onFailure(errorMessage)
            }
        } catch (e: Exception) {

        }

    }

}

