package com.example.outdoortrack.data.model

/**
 * 用户详情信息。
 */
data class UserDetailResponse(
    val id: String,
    val nickname: String?,
    val avatarUrl: String?,
    val signature: String?,
    val clientLanguage: String?
)
