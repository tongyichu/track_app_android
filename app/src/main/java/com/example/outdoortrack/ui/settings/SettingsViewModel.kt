package com.example.outdoortrack.ui.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.outdoortrack.data.repository.AuthRepository
import com.example.outdoortrack.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class SettingsViewModel(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    fun updateAvatar(context: Context, uri: Uri) {
        viewModelScope.launch {
            val userId = userRepository.currentUserId() ?: return@launch
            val file = withContext(Dispatchers.IO) {
                val input = context.contentResolver.openInputStream(uri) ?: return@withContext null
                val temp = File(context.cacheDir, "avatar_temp.jpg")
                temp.outputStream().use { out ->
                    input.copyTo(out)
                }
                temp
            } ?: return@launch
            userRepository.updateAvatar(userId, file)
        }
    }

    fun updateName(name: String) {
        viewModelScope.launch {
            val userId = userRepository.currentUserId() ?: return@launch
            userRepository.updateName(userId, name)
        }
    }

    fun updateSignature(signature: String) {
        viewModelScope.launch {
            val userId = userRepository.currentUserId() ?: return@launch
            userRepository.updateSignature(userId, signature)
        }
    }

    fun updateLanguage(language: String) {
        viewModelScope.launch {
            val userId = userRepository.currentUserId() ?: return@launch
            userRepository.updateClientLanguage(userId, language)
        }
    }

    fun logout() {
        authRepository.logout()
    }

    class Factory(
        private val userRepository: UserRepository,
        private val authRepository: AuthRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(userRepository, authRepository) as T
        }
    }
}
