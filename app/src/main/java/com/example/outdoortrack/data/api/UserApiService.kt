package com.example.outdoortrack.data.api

import com.example.outdoortrack.data.model.UserDetailResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 用户相关接口定义。
 */
interface UserApiService {

    /** 个人信息： GET /api/user/{user_id}/detail */
    @GET("/api/user/{user_id}/detail")
    suspend fun getUserDetail(@Path("user_id") userId: String): UserDetailResponse

    /** 修改个人头像：PUT /api/user/profile/photo?user_id=xxx */
    @Multipart
    @PUT("/api/user/profile/photo")
    suspend fun updateProfilePhoto(
        @Query("user_id") userId: String,
        @Part photo: MultipartBody.Part
    )

    /** 修改用户名：PUT /api/user/profile/name?user_id=xxx */
    @PUT("/api/user/profile/name")
    suspend fun updateUserName(
        @Query("user_id") userId: String,
        @Body name: Map<String, String>
    )

    /** 修改用户签名：PUT /api/user/profile/signature?user_id=xxx */
    @PUT("/api/user/profile/signature")
    suspend fun updateSignature(
        @Query("user_id") userId: String,
        @Body signature: Map<String, String>
    )

    /** 修改客户端语言：PUT /api/user/profile/client_language?user_id=xxx */
    @PUT("/api/user/profile/client_language")
    suspend fun updateClientLanguage(
        @Query("user_id") userId: String,
        @Body language: Map<String, String>
    )
}
