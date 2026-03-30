package com.example.outdoortrack.core.config

/**
 * 一些核心常量与 Header 名称定义。
 */
object AppConfig {
    const val HEADER_USER_ID = "X-User-Id"
    const val HEADER_CLIENT_TYPE = "X-Client-Type"
    const val HEADER_CLIENT_VERSION = "X-Client-Version"
    const val HEADER_CLIENT_LANG = "X-Client-Lang"
    const val HEADER_GEO = "X-Geo"

    const val CLIENT_TYPE_ANDROID = "android"

    // 登录有效期：15 天（毫秒）
    const val SESSION_VALID_DURATION_MILLIS: Long = 15L * 24L * 60L * 60L * 1000L
}
