package com.world.pockyapp.network

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.world.pockyapp.network.models.model.CategoryModel
import com.world.pockyapp.network.models.model.ProductModel
import com.world.pockyapp.network.models.requests.CreateStoreRequestModel
import com.world.pockyapp.network.models.requests.LoginRequestModel
import com.world.pockyapp.network.models.requests.RegisterRequestModel
import com.world.pockyapp.network.models.model.StoreModel
import com.world.pockyapp.network.models.requests.AddProductRequestModel
import com.world.pockyapp.network.models.requests.RequestProductModel
import com.world.pockyapp.network.models.responses.AddProductResponseModel
import com.world.pockyapp.network.models.responses.CategoriesResponseModel
import com.world.pockyapp.network.models.responses.CreateStoreResponseModel
import com.world.pockyapp.network.models.responses.GetProductsResponseModel
import com.world.pockyapp.network.models.responses.GetStoreResponseModel
import com.world.pockyapp.network.models.responses.GetStoresResponseModel
import com.world.pockyapp.network.models.responses.LoginResponseModel
import com.world.pockyapp.network.models.responses.ProductsResponseModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


class ApiManager(val dataStore: DataStore<Preferences>) {

    private val baseUrl = "http://192.168.0.74:3000/api/v1"
    private val client = HttpClient {

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                useAlternativeNames = false
            })
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
        onSuccess: (LoginResponseModel) -> Unit,
        onFailure: (String) -> Unit
    ) {

        val registerRequestModel = RegisterRequestModel(
            firstName = firstName,
            lastName = lastName,
            phone = phone,
            email = email,
            password = password
        )


        //16/3/2021
        val response: HttpResponse = client.post("$baseUrl/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(registerRequestModel)
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
    }

    suspend fun getMyStore(
        onSuccess: (GetStoreResponseModel) -> Unit,
        onFailure: (String) -> Unit
    ) {


        val response: HttpResponse = client.get("$baseUrl/operations/store") {
            contentType(ContentType.Application.Json)
            val token = getToken()
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }

        if (response.status.isSuccess()) {
            val responseBody: GetStoreResponseModel =
                response.body()
            println("ServerSuccess---> ${response.bodyAsText()}")
            onSuccess(responseBody)
        } else {
            val errorMessage: String =
                response.bodyAsText()
            onFailure(errorMessage)
        }
    }

    suspend fun getStore(
        id: String,
        onSuccess: (StoreModel) -> Unit,
        onFailure: (String) -> Unit
    ) {


        val response: HttpResponse = client.get("$baseUrl/operations/store/$id") {
            contentType(ContentType.Application.Json)
            val token = getToken()
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }

        if (response.status.isSuccess()) {
            val responseBody: StoreModel =
                response.body()
            onSuccess(responseBody)
        } else {
            val errorMessage: String =
                response.bodyAsText()
            onFailure(errorMessage)
        }
    }

    suspend fun createStore(
        storeName: String,
        description: String,
        phone: String,
        address: String,
        email: String,
        onSuccess: (CreateStoreResponseModel) -> Unit,
        onFailure: (String) -> Unit
    ) {

        val createStoreRequestModel =
            CreateStoreRequestModel(storeName, description, phone, address, email)
        val response: HttpResponse = client.post("$baseUrl/operations/store") {
            contentType(ContentType.Application.Json)
            val token = getToken()

            println("token-----> $token")
            setBody(createStoreRequestModel)
            headers { append(HttpHeaders.Authorization, "Bearer $token") }
        }

        if (response.status.isSuccess()) {
            val responseBody: CreateStoreResponseModel =
                response.body()
            onSuccess(responseBody)
        } else {
            val errorMessage: String =
                response.bodyAsText()
            onFailure(errorMessage)
        }
    }

    suspend fun addProduct(
        productName: String,
        description: String,
        category: CategoryModel?,
        price: Double,
        stock: Int,
        isNew: Boolean,
        onSuccess: (AddProductResponseModel) -> Unit,
        onFailure: (String) -> Unit
    ) {

        val createStoreRequestModel =
            AddProductRequestModel(productName, category, description, price, stock, isNew)
        val response: HttpResponse = client.post("$baseUrl/operations/product") {
            contentType(ContentType.Application.Json)
            val token = getToken()

            println("token-----> $token")
            setBody(createStoreRequestModel)
            headers { append(HttpHeaders.Authorization, "Bearer $token") }
        }

        if (response.status.isSuccess()) {
            println("success-----> ${response.body() as String}")
            val responseBody: AddProductResponseModel =
                response.body()
            onSuccess(responseBody)
        } else {
            val errorMessage: String =
                response.bodyAsText()
            onFailure(errorMessage)
        }
    }

    suspend fun getCategories(
        onSuccess: (List<CategoryModel>) -> Unit,
        onFailure: (String) -> Unit
    ) {

        val response: HttpResponse = client.get("$baseUrl/operations/categories") {
            contentType(ContentType.Application.Json)
            val token = getToken()

            println("token-----> $token")
            headers { append(HttpHeaders.Authorization, "Bearer $token") }
        }

        if (response.status.isSuccess()) {
            val responseBody: CategoriesResponseModel =
                response.body()
            onSuccess(responseBody.categories)
        } else {
            val errorMessage: String =
                response.bodyAsText()
            onFailure(errorMessage)
        }
    }

    suspend fun getMyProducts(
        onSuccess: (List<ProductModel>) -> Unit,
        onFailure: (String) -> Unit
    ) {

        val response: HttpResponse = client.get("$baseUrl/operations/store/products/me") {
            contentType(ContentType.Application.Json)
            val token = getToken()

            println("token-----> $token")
            headers { append(HttpHeaders.Authorization, "Bearer $token") }
        }

        if (response.status.isSuccess()) {
            val responseBody: ProductsResponseModel =
                response.body()
            println("success-----> ${response.bodyAsText()}")
            onSuccess(responseBody.products)
        } else {
            val errorMessage: String =
                response.bodyAsText()
            onFailure(errorMessage)
        }
    }

    suspend fun getProduct(
        productId: String,
        onSuccess: (ProductModel) -> Unit,
        onFailure: (String) -> Unit
    ) {

        val response: HttpResponse = client.get("$baseUrl/operations/products/$productId") {
            contentType(ContentType.Application.Json)
            val token = getToken()

            println("token-----> $token")
            headers { append(HttpHeaders.Authorization, "Bearer $token") }
        }

        if (response.status.isSuccess()) {
            val responseBody: ProductModel =
                response.body()
            println("success-----> ${response.bodyAsText()}")
            onSuccess(responseBody)
        } else {
            val errorMessage: String =
                response.bodyAsText()
            onFailure(errorMessage)
        }
    }

    suspend fun requestProduct(
        productId: String,
        storeId: String,
        name: String,
        phone: String,
        city: String,
        address: String,
        quantity: Int,
        ownerId: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {

        val requestProductModel =
            RequestProductModel(productId, storeId, name, phone, city, address, quantity, ownerId)
        val response: HttpResponse = client.post("$baseUrl/operations/products") {
            contentType(ContentType.Application.Json)
            val token = getToken()
            setBody(requestProductModel)
            println("token-----> $token")
            headers { append(HttpHeaders.Authorization, "Bearer $token") }
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

    suspend fun getStores(
        onSuccess: (List<StoreModel>) -> Unit,
        onFailure: (String) -> Unit
    ) {

        val response: HttpResponse = client.get("$baseUrl/operations/stores") {
            contentType(ContentType.Application.Json)
            val token = getToken()

            println("token-----> $token")
            headers { append(HttpHeaders.Authorization, "Bearer $token") }
        }

        if (response.status.isSuccess()) {
            val responseBody: GetStoresResponseModel =
                response.body()
            println("success-----> ${response.bodyAsText()}")
            onSuccess(responseBody.stores)
        } else {
            val errorMessage: String =
                response.bodyAsText()
            onFailure(errorMessage)
        }
    }

    suspend fun getProducts(
        onSuccess: (List<ProductModel>) -> Unit,
        onFailure: (String) -> Unit
    ) {

        val response: HttpResponse = client.get("$baseUrl/operations/products") {
            contentType(ContentType.Application.Json)
            val token = getToken()

            println("token-----> $token")
            headers { append(HttpHeaders.Authorization, "Bearer $token") }
        }

        if (response.status.isSuccess()) {
            val responseBody: GetProductsResponseModel =
                response.body()
            println("success-----> ${response.bodyAsText()}")
            onSuccess(responseBody.products)
        } else {
            val errorMessage: String =
                response.bodyAsText()
            onFailure(errorMessage)
        }
    }

}

