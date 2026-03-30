package com.example.outdoortrack.data.repository

import com.example.outdoortrack.core.prefs.AppPreferences
import com.example.outdoortrack.core.config.AppConfig

/**
 * 登录与会话管理仓库：手机号登录（本地 session）与 15 天有效期校验。
 */
class AuthRepository(
    private val appPreferences: AppPreferences
) {

    fun loginWithPhone(phone: String) {
        require(isPhoneValid(phone)) { "invalid phone" }
        // 生成简单的本地 userId，占位实现
        val userId = "phone_${phone}"
        appPreferences.userId = userId
        appPreferences.lastLoginTimestamp = System.currentTimeMillis()
    }

    fun logout() {
        appPreferences.clearSession()
    }

    fun hasValidSession(): Boolean {
        val userId = appPreferences.userId ?: return false
        if (userId.isBlank()) return false
        val last = appPreferences.lastLoginTimestamp
        val now = System.currentTimeMillis()
        return now - last <= AppConfig.SESSION_VALID_DURATION_MILLIS
    }

    companion object {
        /**
         * 简单手机号校验逻辑，单元测试将覆盖该方法。
         */
        fun isPhoneValid(phone: String): Boolean {
            return phone.matches(Regex("^1[3-9]\\d{9}$"))
        }
    }
}
