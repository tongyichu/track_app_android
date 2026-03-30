package com.outdoor.trail.data.remote

import com.outdoor.trail.BuildConfig
import com.outdoor.trail.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit网络客户端单例
 * 自动在所有请求中添加公共Header：
 * X-User-ID, X-Client-Type, X-Client-Version, X-Client-Language, X-Geo-Location
 */
object ApiClient {

    private const val CONNECT_TIMEOUT = 15L
    private const val READ_TIMEOUT = 30L
    private const val WRITE_TIMEOUT = 30L

    /** 当前用户地理位置（由定位服务更新） */
    @Volatile
    var currentLocation: String = ""

    private val headerInterceptor = Interceptor { chain ->
        val original = chain.request()
        val builder = original.newBuilder()
            .header("X-User-ID", TokenManager.getUserId())
            .header("X-Client-Type", "android")
            .header("X-Client-Version", BuildConfig.VERSION_NAME)
            .header("X-Client-Language", TokenManager.getClientLanguage())
            .header("X-Geo-Location", currentLocation)

        // 如果有JWT token则添加Authorization header
        val token = TokenManager.getToken()
        if (token.isNotEmpty()) {
            builder.header("Authorization", "Bearer $token")
        }

        chain.proceed(builder.build())
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
        .addInterceptor(headerInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL + "/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /** 获取API Service实例 */
    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
