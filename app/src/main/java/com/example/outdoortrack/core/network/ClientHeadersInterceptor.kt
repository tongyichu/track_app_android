package com.example.outdoortrack.core.network

import com.example.outdoortrack.BuildConfig
import com.example.outdoortrack.core.config.AppConfig
import com.example.outdoortrack.core.location.LocationProvider
import com.example.outdoortrack.core.prefs.AppPreferences
import okhttp3.Interceptor
import okhttp3.Response
import java.util.Locale

/**
 * 统一为所有接口添加约定 Header：
 * - X-User-Id
 * - X-Client-Type
 * - X-Client-Version
 * - X-Client-Lang
 * - X-Geo
 */
class ClientHeadersInterceptor(
    private val appPreferences: AppPreferences,
    private val locationProvider: LocationProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()

        val userId = appPreferences.userId.orEmpty()
        val clientLang = appPreferences.clientLanguage.ifBlank {
            Locale.getDefault().toLanguageTag()
        }
        val geo = locationProvider.geoHeaderString() ?: "0,0" // 不可用时使用占位

        builder.header(AppConfig.HEADER_USER_ID, userId)
        builder.header(AppConfig.HEADER_CLIENT_TYPE, AppConfig.CLIENT_TYPE_ANDROID)
        builder.header(AppConfig.HEADER_CLIENT_VERSION, BuildConfig.X_CLIENT_VERSION)
        builder.header(AppConfig.HEADER_CLIENT_LANG, clientLang)
        builder.header(AppConfig.HEADER_GEO, geo)

        return chain.proceed(builder.build())
    }
}
