package com.outdoor.trail.data.repository

import com.outdoor.trail.data.model.*
import com.outdoor.trail.data.remote.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 用户数据仓库，封装所有用户相关的网络请求
 */
class UserRepository {

    private val api = ApiClient.apiService

    /** 微信登录 */
    suspend fun wechatLogin(code: String): Result<LoginResponse> = safeApiCall {
        api.wechatLogin(WechatLoginRequest(code))
    }

    /** 手机号登录 */
    suspend fun phoneLogin(phone: String, smsCode: String): Result<LoginResponse> = safeApiCall {
        api.phoneLogin(PhoneLoginRequest(phone, smsCode))
    }

    /** 获取用户详情 */
    suspend fun getUserDetail(userId: String): Result<UserDetailResponse> = safeApiCall {
        api.getUserDetail(userId)
    }

    /** 修改头像 */
    suspend fun updatePhoto(userId: String, avatarUrl: String): Result<Any> = safeApiCall {
        api.updatePhoto(userId, UpdatePhotoRequest(avatarUrl))
    }

    /** 修改用户名 */
    suspend fun updateName(userId: String, nickname: String): Result<Any> = safeApiCall {
        api.updateName(userId, UpdateNameRequest(nickname))
    }

    /** 修改签名 */
    suspend fun updateSignature(userId: String, signature: String): Result<Any> = safeApiCall {
        api.updateSignature(userId, UpdateSignatureRequest(signature))
    }

    /** 修改客户端语言 */
    suspend fun updateLanguage(userId: String, language: String): Result<Any> = safeApiCall {
        api.updateClientLanguage(userId, UpdateLanguageRequest(language))
    }

    private suspend fun <T> safeApiCall(
        call: suspend () -> retrofit2.Response<ApiResponse<T>>
    ): Result<T> = withContext(Dispatchers.IO) {
        try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.code == 0 && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.message ?: "Unknown error"))
                }
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
