package com.outdoor.trail.data.remote

import com.outdoor.trail.data.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API接口定义
 * 对应服务端所有API端点，所有公共Header由ApiClient拦截器自动添加
 */
interface ApiService {

    // ===== 认证接口 =====

    /** 微信登录 */
    @POST("api/auth/wechat")
    suspend fun wechatLogin(@Body request: WechatLoginRequest): Response<ApiResponse<LoginResponse>>

    /** 手机号登录 */
    @POST("api/auth/phone")
    suspend fun phoneLogin(@Body request: PhoneLoginRequest): Response<ApiResponse<LoginResponse>>

    // ===== 轨迹接口 =====

    /** 创建轨迹记录 */
    @POST("api/track/create")
    suspend fun createTrack(@Body request: CreateTrackRequest): Response<ApiResponse<CreateTrackResponse>>

    /** 获取推荐轨迹列表 */
    @GET("api/track/recommend/list")
    suspend fun getRecommendList(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): Response<ApiResponse<TrackListResponse>>

    /** 搜索轨迹列表 */
    @GET("api/track/search/list")
    suspend fun searchTracks(
        @Query("keyword") keyword: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): Response<ApiResponse<TrackListResponse>>

    /** 查询是否存在正在进行中的轨迹 */
    @GET("api/track/running")
    suspend fun getRunningTrack(
        @Query("user_id") userId: String
    ): Response<ApiResponse<RunningTrackResponse>>

    /** 获取轨迹地图数据 */
    @GET("api/track/{track_id}/map")
    suspend fun getTrackMap(
        @Path("track_id") trackId: String
    ): Response<ApiResponse<TrackMapResponse>>

    /** 获取轨迹详情 */
    @GET("api/track/{track_id}/detail")
    suspend fun getTrackDetail(
        @Path("track_id") trackId: String
    ): Response<ApiResponse<TrackDetailResponse>>

    /** 获取轨迹摘要（他人轨迹） */
    @GET("api/track/{track_id}/summary")
    suspend fun getTrackSummary(
        @Path("track_id") trackId: String
    ): Response<ApiResponse<TrackSummaryResponse>>

    /** 上传轨迹到云端 */
    @POST("api/track/{track_id}/upload_cloud")
    suspend fun uploadToCloud(
        @Path("track_id") trackId: String
    ): Response<ApiResponse<Any>>

    // ===== 收藏接口 =====

    /** 查询收藏状态 */
    @GET("api/user/{user_id}/collect")
    suspend fun getCollectStatus(
        @Path("user_id") userId: String,
        @Query("track_id") trackId: String
    ): Response<ApiResponse<CollectStatusResponse>>

    /** 收藏轨迹 */
    @POST("api/track_collect")
    suspend fun collectTrack(
        @Query("track_id") trackId: String,
        @Query("user_id") userId: String
    ): Response<ApiResponse<Any>>

    /** 取消收藏轨迹 */
    @DELETE("api/track_collect")
    suspend fun uncollectTrack(
        @Query("track_id") trackId: String,
        @Query("user_id") userId: String
    ): Response<ApiResponse<Any>>

    // ===== 用户接口 =====

    /** 获取用户详情 */
    @GET("api/user/{user_id}/detail")
    suspend fun getUserDetail(
        @Path("user_id") userId: String
    ): Response<ApiResponse<UserDetailResponse>>

    /** 修改头像 */
    @PUT("api/user/profile/photo")
    suspend fun updatePhoto(
        @Query("user_id") userId: String,
        @Body request: UpdatePhotoRequest
    ): Response<ApiResponse<Any>>

    /** 修改用户名 */
    @PUT("api/user/profile/name")
    suspend fun updateName(
        @Query("user_id") userId: String,
        @Body request: UpdateNameRequest
    ): Response<ApiResponse<Any>>

    /** 修改用户签名 */
    @PUT("api/user/profile/signature")
    suspend fun updateSignature(
        @Query("user_id") userId: String,
        @Body request: UpdateSignatureRequest
    ): Response<ApiResponse<Any>>

    /** 修改客户端语言 */
    @PUT("api/user/profile/client_language")
    suspend fun updateClientLanguage(
        @Query("user_id") userId: String,
        @Body request: UpdateLanguageRequest
    ): Response<ApiResponse<Any>>
}
