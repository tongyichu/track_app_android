package com.outdoor.trail.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.outdoor.trail.data.repository.UserRepository
import kotlinx.coroutines.launch

/**
 * 设置页ViewModel
 */
class SettingsViewModel : ViewModel() {
    private val repository = UserRepository()

    private val _updateSuccess = MutableLiveData<String>()
    val updateSuccess: LiveData<String> = _updateSuccess

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun updatePhoto(userId: String, url: String) {
        viewModelScope.launch {
            repository.updatePhoto(userId, url).fold(
                onSuccess = { _updateSuccess.value = "头像已更新" },
                onFailure = { _error.value = it.message }
            )
        }
    }

    fun updateName(userId: String, name: String) {
        viewModelScope.launch {
            repository.updateName(userId, name).fold(
                onSuccess = { _updateSuccess.value = "用户名已更新" },
                onFailure = { _error.value = it.message }
            )
        }
    }

    fun updateSignature(userId: String, signature: String) {
        viewModelScope.launch {
            repository.updateSignature(userId, signature).fold(
                onSuccess = { _updateSuccess.value = "签名已更新" },
                onFailure = { _error.value = it.message }
            )
        }
    }

    fun updateLanguage(userId: String, lang: String) {
        viewModelScope.launch {
            repository.updateLanguage(userId, lang).fold(
                onSuccess = { _updateSuccess.value = "语言已更新" },
                onFailure = { _error.value = it.message }
            )
        }
    }
}
