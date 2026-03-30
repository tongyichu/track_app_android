package com.example.outdoortrack.data.repository

import com.example.outdoortrack.core.prefs.AppPreferences
import com.example.outdoortrack.data.api.UserApiService
import com.example.outdoortrack.data.model.UserDetailResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import java.io.File

/**
 * 用户信息相关仓库：个人中心与设置页面使用。
 */
class UserRepository(
    retrofit: Retrofit,
    private val appPreferences: AppPreferences
) {
    private val api = retrofit.create(UserApiService::class.java)

    suspend fun getUserDetail(userId: String): UserDetailResponse = api.getUserDetail(userId)

    suspend fun updateAvatar(userId: String, file: File) {
        val body = file.asRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("photo", file.name, body)
        api.updateProfilePhoto(userId, part)
    }

    suspend fun updateName(userId: String, name: String) {
        api.updateUserName(userId, mapOf("name" to name))
    }

    suspend fun updateSignature(userId: String, signature: String) {
        api.updateSignature(userId, mapOf("signature" to signature))
    }

    suspend fun updateClientLanguage(userId: String, language: String) {
        api.updateClientLanguage(userId, mapOf("client_language" to language))
        appPreferences.clientLanguage = language
    }

    fun currentUserId(): String? = appPreferences.userId
}
