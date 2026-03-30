package com.outdoor.trail.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Token管理器，负责JWT token和用户信息的安全存储
 * 使用EncryptedSharedPreferences加密存储敏感信息
 * 登录有效期15天，超过15天未使用自动过期
 */
object TokenManager {

    private const val PREF_NAME = "outdoor_trail_auth"
    private const val KEY_TOKEN = "jwt_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_NICKNAME = "nickname"
    private const val KEY_AVATAR = "avatar_url"
    private const val KEY_LAST_ACTIVE = "last_active_time"
    private const val KEY_CLIENT_LANGUAGE = "client_language"
    private const val LOGIN_EXPIRE_MILLIS = 15L * 24 * 60 * 60 * 1000 // 15天

    private lateinit var prefs: SharedPreferences

    /**
     * 初始化TokenManager，创建加密SharedPreferences
     */
    fun init(context: Context) {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        prefs = EncryptedSharedPreferences.create(
            context,
            PREF_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /**
     * 保存登录信息
     */
    fun saveLoginInfo(token: String, userId: String, nickname: String, avatar: String) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_USER_ID, userId)
            .putString(KEY_NICKNAME, nickname)
            .putString(KEY_AVATAR, avatar)
            .putLong(KEY_LAST_ACTIVE, System.currentTimeMillis())
            .apply()
    }

    /**
     * 更新最后活跃时间（每次打开app时调用）
     */
    fun updateLastActive() {
        prefs.edit().putLong(KEY_LAST_ACTIVE, System.currentTimeMillis()).apply()
    }

    /**
     * 检查登录是否有效（15天内有活跃记录）
     */
    fun isLoggedIn(): Boolean {
        val token = prefs.getString(KEY_TOKEN, null) ?: return false
        if (token.isEmpty()) return false
        val lastActive = prefs.getLong(KEY_LAST_ACTIVE, 0)
        return (System.currentTimeMillis() - lastActive) < LOGIN_EXPIRE_MILLIS
    }

    fun getToken(): String = prefs.getString(KEY_TOKEN, "") ?: ""
    fun getUserId(): String = prefs.getString(KEY_USER_ID, "") ?: ""
    fun getNickname(): String = prefs.getString(KEY_NICKNAME, "") ?: ""
    fun getAvatar(): String = prefs.getString(KEY_AVATAR, "") ?: ""

    fun getClientLanguage(): String = prefs.getString(KEY_CLIENT_LANGUAGE, "zh-CN") ?: "zh-CN"
    fun setClientLanguage(lang: String) {
        prefs.edit().putString(KEY_CLIENT_LANGUAGE, lang).apply()
    }

    /**
     * 退出登录，清除所有认证信息
     */
    fun logout() {
        prefs.edit().clear().apply()
    }
}
