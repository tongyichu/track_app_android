package com.outdoor.trail.data.model

import com.google.gson.annotations.SerializedName

/**
 * 通用API响应包装类
 */
data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T?
)

/**
 * 微信登录请求
 */
data class WechatLoginRequest(val code: String)

/**
 * 手机号登录请求
 */
data class PhoneLoginRequest(val phone: String, val code: String)

/**
 * 登录响应
 */
data class LoginResponse(
    val token: String,
    @SerializedName("user_id") val userId: String,
    val nickname: String,
    @SerializedName("avatar_url") val avatarUrl: String
)

/**
 * 创建轨迹请求
 */
data class CreateTrackRequest(
    @SerializedName("sport_type") val sportType: String,
    val title: String,
    val longitude: Double,
    val latitude: Double,
    val altitude: Double
)

/**
 * 创建轨迹响应
 */
data class CreateTrackResponse(
    @SerializedName("track_id") val trackId: String
)

/**
 * GPS坐标点
 */
data class GeoPoint(
    val longitude: Double,
    val latitude: Double,
    val altitude: Double,
    val speed: Double,
    val bearing: Double,
    val accuracy: Double,
    val timestamp: String
)

/**
 * 轨迹地图数据响应
 */
data class TrackMapResponse(
    @SerializedName("track_id") val trackId: String,
    val status: Int,
    val points: List<GeoPoint>,
    @SerializedName("center_lng") val centerLng: Double,
    @SerializedName("center_lat") val centerLat: Double
)

/**
 * 轨迹详情响应
 */
data class TrackDetailResponse(
    @SerializedName("track_id") val trackId: String,
    val title: String,
    val status: Int,
    @SerializedName("sport_type") val sportType: String,
    val distance: Double,
    val duration: Long,
    @SerializedName("avg_speed") val avgSpeed: Double,
    @SerializedName("max_speed") val maxSpeed: Double,
    @SerializedName("elevation_gain") val elevationGain: Double,
    @SerializedName("elevation_loss") val elevationLoss: Double,
    @SerializedName("max_altitude") val maxAltitude: Double,
    @SerializedName("min_altitude") val minAltitude: Double,
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String?,
    @SerializedName("cover_image") val coverImage: String,
    val region: String,
    val description: String
)

/**
 * 轨迹摘要响应（他人轨迹详情用）
 */
data class TrackSummaryResponse(
    @SerializedName("track_id") val trackId: String,
    val title: String,
    @SerializedName("sport_type") val sportType: String,
    val distance: Double,
    val duration: Long,
    @SerializedName("elevation_gain") val elevationGain: Double,
    val region: String,
    @SerializedName("cover_image") val coverImage: String,
    @SerializedName("user_nickname") val userNickname: String,
    @SerializedName("user_avatar") val userAvatar: String,
    @SerializedName("collect_count") val collectCount: Int
)

/**
 * 轨迹列表项
 */
data class TrackListItem(
    @SerializedName("track_id") val trackId: String,
    val title: String,
    @SerializedName("sport_type") val sportType: String,
    val distance: Double,
    val duration: Long,
    val region: String,
    @SerializedName("cover_image") val coverImage: String,
    @SerializedName("user_nickname") val userNickname: String,
    @SerializedName("user_avatar") val userAvatar: String,
    @SerializedName("collect_count") val collectCount: Int
)

/**
 * 轨迹列表响应（带分页）
 */
data class TrackListResponse(
    val total: Long,
    val page: Int,
    val size: Int,
    val tracks: List<TrackListItem>
)

/**
 * 正在进行中的轨迹响应
 */
data class RunningTrackResponse(
    @SerializedName("has_running") val hasRunning: Boolean,
    @SerializedName("track_id") val trackId: String?
)

/**
 * 收藏状态响应
 */
data class CollectStatusResponse(
    @SerializedName("is_collected") val isCollected: Boolean
)

/**
 * 用户详情响应
 */
data class UserDetailResponse(
    val id: String,
    val nickname: String,
    @SerializedName("avatar_url") val avatarUrl: String,
    val signature: String,
    val phone: String,
    @SerializedName("client_language") val clientLanguage: String,
    @SerializedName("total_distance") val totalDistance: Double,
    @SerializedName("total_duration") val totalDuration: Long,
    @SerializedName("track_count") val trackCount: Int
)

// ===== 更新请求 =====

data class UpdatePhotoRequest(@SerializedName("avatar_url") val avatarUrl: String)
data class UpdateNameRequest(val nickname: String)
data class UpdateSignatureRequest(val signature: String)
data class UpdateLanguageRequest(@SerializedName("client_language") val clientLanguage: String)
