package com.world.pockyapp.screens.create_challenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.models.model.ErrorModel
import com.world.pockyapp.network.models.model.ResponseMessageModel
import com.world.pockyapp.screens.auth.register.RegisterScreenViewModel.RegisterUiState
import com.world.pockyapp.screens.settings.controlAccount.ResponseState
import com.world.pockyapp.utils.Utils.isValidEmail
import com.world.pockyapp.utils.Utils.isValidPhoneNumber
import com.world.pockyapp.utils.Utils.isValidUsername
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateChallengeViewModel(private val sdk : ApiManager) : ViewModel() {


    var video: ByteArray? = null

    var title: String = ""
    var description: String = ""
    var rules: String = ""
    var category: String = ""
    var difficulty: String = ""


    private val _createChallengeState = MutableStateFlow<ResponseState<ResponseMessageModel>>(ResponseState.Idle)
    val createChallengeState = _createChallengeState.asStateFlow()
    fun createChallenge(){
        viewModelScope.launch {

            if (video == null) {
                _createChallengeState.value = ResponseState.Error(
                    error = ErrorModel(
                        message = "Add video",
                        code = 400
                    )
                )
                return@launch
            }

            if (title.isEmpty()) {
                _createChallengeState.value = ResponseState.Error(
                    error = ErrorModel(
                        message = "Add title",
                        code = 400
                    )
                )
                return@launch
            }

            if (description.isEmpty()) {
                _createChallengeState.value = ResponseState.Error(
                    error = ErrorModel(
                        message = "Add description",
                        code = 400
                    )
                )
                return@launch
            }

            if (rules.isEmpty()) {
                _createChallengeState.value = ResponseState.Error(
                    error = ErrorModel(
                        message = "Add rules",
                        code = 400
                    )
                )
                return@launch
            }

            if (category.isEmpty()) {
                _createChallengeState.value = ResponseState.Error(
                    error = ErrorModel(
                        message = "Add category",
                        code = 400
                    )
                )
                return@launch
            }

            if (difficulty.isEmpty()) {
                _createChallengeState.value = ResponseState.Error(
                    error = ErrorModel(
                        message = "Add difficulty",
                        code = 400
                    )
                )
                return@launch
            }


            _createChallengeState.value = ResponseState.Loading
            sdk.createChallenge(video!!, title, description, rules, category, difficulty,{
                _createChallengeState.value = ResponseState.Success(it)
            },{
                _createChallengeState.value = ResponseState.Error(it)
            })

        }
    }


}