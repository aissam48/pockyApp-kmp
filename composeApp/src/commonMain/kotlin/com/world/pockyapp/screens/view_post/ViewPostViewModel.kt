package com.world.pockyapp.screens.view_post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.models.model.PostModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ViewPostViewModel(val sdk: ApiManager) : ViewModel() {

    private val _postState = MutableStateFlow<PostModel?>(PostModel())
    val postState = _postState.asStateFlow()

    private val _likeState = MutableStateFlow<String>("")
    val likeState = _likeState.asStateFlow()

    private val _unLikeState = MutableStateFlow<String>("")
    val unLikeState = _unLikeState.asStateFlow()

    private val _deleteState = MutableStateFlow<String>("")
    val deleteState = _deleteState.asStateFlow()

    fun getPost(postID: String) {
        viewModelScope.launch {
            sdk.getPost(postID, { success ->
                _postState.value = success
            }, { error ->
                _postState.value = null
            })
        }
    }

    fun deletePost(postID: String) {
        viewModelScope.launch {
            sdk.deletePost(postID, { success ->
                _deleteState.value = success
            }, { error ->
                _deleteState.value = ""
            })
        }
    }

    fun like(postID: String) {
        viewModelScope.launch {
            sdk.like(postID, { success ->
                _likeState.value = success
            }, { error ->
                _likeState.value = ""
            })
        }
    }

    fun unLike(postID: String) {
        viewModelScope.launch {
            sdk.unLike(postID, { success ->
                _unLikeState.value = success
            }, { error ->
                _unLikeState.value = ""
            })
        }
    }


}