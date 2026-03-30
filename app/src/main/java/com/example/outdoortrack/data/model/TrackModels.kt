package com.example.outdoortrack.data.model

/**
 * 轨迹列表项（推荐/搜索）。真实字段可根据服务端约定调整。
 */
data class TrackListItem(
    val id: String,
    val name: String,
    val distanceMeters: Double?,
    val durationSeconds: Long?,
    val thumbnailUrl: String?,
    val ownerName: String?
)

/**
 * 轨迹地图返回：包含折线点集合。
 */
data class TrackMapResponse(
    val trackId: String,
    val points: List<TrackPointDto>
)

/**
 * 单个轨迹采样点。
 */
data class TrackPointDto(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double?,
    val timestamp: Long
)

/**
 * 轨迹详情信息（正在记录 / 结束总结）。
 */
data class TrackDetailResponse(
    val id: String,
    val name: String?,
    val distanceMeters: Double?,
    val durationSeconds: Long?,
    val elevationGain: Double?,
    val avgPace: Double?,
    val status: String? // running / finished 等
)

/**
 * 他人轨迹扼要信息。
 */
data class TrackSummaryResponse(
    val id: String,
    val name: String?,
    val distanceMeters: Double?,
    val durationSeconds: Long?,
    val elevationGain: Double?
)

/**
 * 上传轨迹到云端时的请求体，占位设计：携带采样点与汇总统计。
 */
data class TrackUploadRequest(
    val trackId: String,
    val points: List<TrackPointDto>,
    val distanceMeters: Double?,
    val durationSeconds: Long?,
    val elevationGain: Double?,
    val avgPace: Double?
)
