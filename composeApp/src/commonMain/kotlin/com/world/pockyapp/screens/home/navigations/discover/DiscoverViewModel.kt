package com.world.pockyapp.screens.home.navigations.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.models.model.ErrorModel
import com.world.pockyapp.network.models.model.MomentModel
import com.world.pockyapp.network.models.model.PostModel
import com.world.pockyapp.network.models.model.ProfileModel
import com.world.pockyapp.screens.settings.controlAccount.ResponseState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val error: ErrorModel) : UiState<Nothing>()
}

data class LikeAction(
    val postId: String,
    val isLiked: Boolean
)

class DiscoverViewModel(private val sdk: ApiManager) : ViewModel() {

    private var isProfileLoadingFirstTime = true
    private var isFriendsMomentsLoadingFirstTime = true
    private var isMyDailyMomentsLoadingFirstTime = true
    private var isNearbyMomentsLoadingFirstTime = true
    private var isNearbyPostsLoadingFirstTime = true

    private val _profileState = MutableStateFlow<UiState<ProfileModel>>(UiState.Loading)
    val profileState: StateFlow<UiState<ProfileModel>> = _profileState.asStateFlow()

    private val _myDailyMomentsState =
        MutableStateFlow<UiState<List<MomentModel>>>(UiState.Loading)
    val myDailyMomentsState: StateFlow<UiState<List<MomentModel>>> =
        _myDailyMomentsState.asStateFlow()

    private val _friendsMomentsState =
        MutableStateFlow<UiState<List<MomentModel>>>(UiState.Loading)
    val friendsMomentsState: StateFlow<UiState<List<MomentModel>>> =
        _friendsMomentsState.asStateFlow()

    private val _nearbyMomentsState = MutableStateFlow<UiState<List<MomentModel>>>(UiState.Loading)
    val nearbyMomentsState: StateFlow<UiState<List<MomentModel>>> =
        _nearbyMomentsState.asStateFlow()

    private val _nearbyPostsState = MutableStateFlow<UiState<List<PostModel>>>(UiState.Loading)
    val nearbyPostsState: StateFlow<UiState<List<PostModel>>> = _nearbyPostsState.asStateFlow()

    private val _likeActionState = MutableStateFlow<UiState<LikeAction>?>(null)
    val likeActionState: StateFlow<UiState<LikeAction>?> = _likeActionState.asStateFlow()


    fun getProfile() {
        viewModelScope.launch {
            if (isProfileLoadingFirstTime) {
                _profileState.value = UiState.Loading
            }
            try {
                sdk.getMyProfile(
                    onSuccess = { profile ->
                        isProfileLoadingFirstTime = false
                        _profileState.value = UiState.Success(profile)
                    },
                    onFailure = { error ->
                        isProfileLoadingFirstTime = true
                        _profileState.value = UiState.Error(error)
                    }
                )
            } catch (e: Exception) {
                _profileState.value = UiState.Error(
                    error = ErrorModel(
                        message = "Network error. Please try again later.",
                        code = 500
                    )
                )
            }
        }
    }

    fun loadMyDailyMoments() {
        viewModelScope.launch {
            if (isMyDailyMomentsLoadingFirstTime) {
                _myDailyMomentsState.value = UiState.Loading
            }
            try {
                sdk.getMyDailyMoments(
                    onSuccess = { moments ->
                        isMyDailyMomentsLoadingFirstTime = false
                        _myDailyMomentsState.value = UiState.Success(moments)
                    },
                    onFailure = { error ->
                        isMyDailyMomentsLoadingFirstTime = true
                        _myDailyMomentsState.value = UiState.Error(error)
                    }
                )
            } catch (e: Exception) {
                _myDailyMomentsState.value = UiState.Error(
                    error = ErrorModel(
                        message = "Network error. Please try again later.",
                        code = 500
                    )
                )
            }
        }
    }


    fun loadFriendsMoments() {
        viewModelScope.launch {
            if (isFriendsMomentsLoadingFirstTime) {
                _friendsMomentsState.value = UiState.Loading
            }
            try {
                sdk.getFriendsMoments(
                    onSuccess = { moments ->
                        isFriendsMomentsLoadingFirstTime = false
                        _friendsMomentsState.value = UiState.Success(moments)
                    },
                    onFailure = { error ->
                        isFriendsMomentsLoadingFirstTime = true
                        _friendsMomentsState.value = UiState.Error(error)
                    }
                )
            } catch (e: Exception) {
                _friendsMomentsState.value = UiState.Error(
                    error = ErrorModel(
                        message = "Network error. Please try again later.",
                        code = 500
                    )
                )
            }
        }
    }

    fun loadNearbyMoments() {
        viewModelScope.launch {
            if (isNearbyMomentsLoadingFirstTime) {
                _nearbyMomentsState.value = UiState.Loading
            }
            try {
                sdk.getNearbyMoments(
                    onSuccess = { moments ->
                        isNearbyMomentsLoadingFirstTime = false
                        _nearbyMomentsState.value = UiState.Success(moments)
                    },
                    onFailure = { error ->
                        isNearbyMomentsLoadingFirstTime = true
                        _nearbyMomentsState.value = UiState.Error(error)
                    }
                )
            } catch (e: Exception) {
                _nearbyMomentsState.value = UiState.Error(
                    error = ErrorModel(
                        message = "Network error. Please try again later.",
                        code = 500
                    )
                )
            }
        }
    }

    fun loadNearbyPosts() {
        viewModelScope.launch {
            if (isNearbyPostsLoadingFirstTime) {
                _nearbyPostsState.value = UiState.Loading
            }
            try {
                sdk.getNearbyPosts(
                    onSuccess = { posts ->
                        isNearbyPostsLoadingFirstTime = false
                        _nearbyPostsState.value = UiState.Success(posts)
                    },
                    onFailure = { error ->
                        isNearbyPostsLoadingFirstTime = true
                        _nearbyPostsState.value = UiState.Error(error)
                    }
                )
            } catch (e: Exception) {
                _nearbyPostsState.value = UiState.Error(
                    error = ErrorModel(
                        message = "Network error. Please try again later.",
                        code = 500
                    )
                )
            }
        }
    }

    fun toggleLike(postId: String, userId: String, isLiked: Boolean) {
        viewModelScope.launch {
            _likeActionState.value = UiState.Loading
            try {
                if (!isLiked){
                    sdk.unLike(postId, {}, {})

                }else{
                    sdk.like(postId, {}, {})

                }

            } catch (e: Exception) {
                _likeActionState.value = UiState.Error(
                    error = ErrorModel(
                        message = "Network error. Please try again later.",
                        code = 500
                    )
                )
            }
        }
    }

    private var isFollowingsMomentsLoadingFirstTime = true
    private val _followingsMomentsState = MutableStateFlow<ResponseState<List<MomentModel>>>(ResponseState.Idle)
    val followingsMomentsState = _followingsMomentsState.asStateFlow()

    fun loadFollowingsMoments() {
        viewModelScope.launch {
            if (isFollowingsMomentsLoadingFirstTime) {
                _followingsMomentsState.value = ResponseState.Loading
            }
            try {
                sdk.getFollowingsMoments(
                    onSuccess = { moments ->
                        isFollowingsMomentsLoadingFirstTime = false
                        _followingsMomentsState.value = ResponseState.Success(moments)
                    },
                    onFailure = { error ->
                        isFollowingsMomentsLoadingFirstTime = true
                        _followingsMomentsState.value = ResponseState.Error(error)
                    }
                )
            } catch (e: Exception) {
                _followingsMomentsState.value = ResponseState.Error(
                    error = ErrorModel(
                        message = "Network error. Please try again later.",
                        code = 500
                    )
                )
            }
        }
    }

    private var isFollowingsPostsLoadingFirstTime = true
    private val _followingsPostsState = MutableStateFlow<ResponseState<List<PostModel>>>(ResponseState.Idle)
    val followingsPostsState = _followingsPostsState.asStateFlow()

    fun loadFollowingsPosts() {
        viewModelScope.launch {
            if (isFollowingsPostsLoadingFirstTime) {
                _followingsPostsState.value = ResponseState.Loading
            }
            try {
                sdk.getFollowingsPosts(
                    onSuccess = { posts ->
                        isFollowingsPostsLoadingFirstTime = false
                        _followingsPostsState.value = ResponseState.Success(posts)
                    },
                    onFailure = { error ->
                        isFollowingsPostsLoadingFirstTime = true
                        _followingsPostsState.value = ResponseState.Error(error)
                    }
                )
            } catch (e: Exception) {
                _followingsPostsState.value = ResponseState.Error(
                    error = ErrorModel(
                        message = "Network error. Please try again later.",
                        code = 500
                    )
                )
            }
        }
    }
}