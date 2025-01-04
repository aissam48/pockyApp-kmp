package com.world.pockyapp.screens.view_post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.models.model.ErrorModel
import com.world.pockyapp.network.models.model.PostModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Define ResultState for each operation separately
sealed class PostResultState {
    object Loading : PostResultState()
    data class Success(val post: PostModel) : PostResultState()
    data class Error(val error: ErrorModel) : PostResultState()
}

sealed class LikeResultState {
    object Loading : LikeResultState()
    data class Success(val message: String) : LikeResultState()
    data class Error(val error: ErrorModel) : LikeResultState()
}

sealed class UnLikeResultState {
    object Loading : UnLikeResultState()
    data class Success(val message: String) : UnLikeResultState()
    data class Error(val error: ErrorModel) : UnLikeResultState()
}

sealed class DeleteResultState {
    object Loading : DeleteResultState()
    data class Success(val message: String) : DeleteResultState()
    data class Error(val error: ErrorModel) : DeleteResultState()
}

class ViewPostViewModel(val sdk: ApiManager) : ViewModel() {

    private val _postState = MutableStateFlow<PostResultState>(PostResultState.Loading)
    val postState = _postState.asStateFlow()

    private val _likeState = MutableStateFlow<LikeResultState>(LikeResultState.Loading)
    val likeState = _likeState.asStateFlow()

    private val _unLikeState = MutableStateFlow<UnLikeResultState>(UnLikeResultState.Loading)
    val unLikeState = _unLikeState.asStateFlow()

    private val _deleteState = MutableStateFlow<DeleteResultState>(DeleteResultState.Loading)
    val deleteState = _deleteState.asStateFlow()

    fun getPost(postID: String) {
        _postState.value = PostResultState.Loading
        viewModelScope.launch {
            sdk.getPost(postID, { success ->
                _postState.value = PostResultState.Success(success)
            }, { error ->
                _postState.value = PostResultState.Error(error)
            })
        }
    }

    fun deletePost(postID: String) {
        _deleteState.value = DeleteResultState.Loading
        viewModelScope.launch {
            sdk.deletePost(postID, { success ->
                _deleteState.value = DeleteResultState.Success(success)
            }, { error ->
                _deleteState.value = DeleteResultState.Error(error)
            })
        }
    }

    fun like(postID: String) {
        _likeState.value = LikeResultState.Loading
        viewModelScope.launch {
            sdk.like(postID, { success ->
                _likeState.value = LikeResultState.Success(success)
            }, { error ->
                _likeState.value = LikeResultState.Error(error)
            })
        }
    }

    fun unLike(postID: String) {
        _unLikeState.value = UnLikeResultState.Loading
        viewModelScope.launch {
            sdk.unLike(postID, { success ->
                _unLikeState.value = UnLikeResultState.Success(success)
            }, { error ->
                _unLikeState.value = UnLikeResultState.Error(error)
            })
        }
    }
}
