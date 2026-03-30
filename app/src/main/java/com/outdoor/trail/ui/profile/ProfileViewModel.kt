package com.outdoor.trail.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.outdoor.trail.data.model.UserDetailResponse
import com.outdoor.trail.data.repository.UserRepository
import kotlinx.coroutines.launch

/**
 * 个人中心ViewModel
 */
class ProfileViewModel : ViewModel() {
    private val repository = UserRepository()

    private val _userDetail = MutableLiveData<UserDetailResponse>()
    val userDetail: LiveData<UserDetailResponse> = _userDetail

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadUserDetail(userId: String) {
        viewModelScope.launch {
            repository.getUserDetail(userId).fold(
                onSuccess = { _userDetail.value = it },
                onFailure = { _error.value = it.message }
            )
        }
    }
}
