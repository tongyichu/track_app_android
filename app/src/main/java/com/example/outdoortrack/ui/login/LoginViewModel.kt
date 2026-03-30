package com.example.outdoortrack.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.outdoortrack.data.repository.AuthRepository

/**
 * 登录页 ViewModel，封装手机号登录逻辑与 Session 管理。
 */
class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    fun loginWithPhone(phone: String) {
        authRepository.loginWithPhone(phone)
    }

    class Factory(
        private val authRepository: AuthRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(authRepository) as T
        }
    }
}
