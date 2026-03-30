package com.example.outdoortrack.core

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.outdoortrack.core.location.LocationProvider
import com.example.outdoortrack.core.network.ClientHeadersInterceptor
import com.example.outdoortrack.core.network.NetworkModule
import com.example.outdoortrack.core.prefs.AppPreferences
import com.example.outdoortrack.data.local.TrackDatabase
import com.example.outdoortrack.data.repository.AuthRepository
import com.example.outdoortrack.data.repository.TrackRepository
import com.example.outdoortrack.data.repository.UserRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit

/**
 * 简单的 ServiceLocator，用于集中管理应用中的单例依赖，避免引入 DI 框架。
 */
object ServiceLocator {

    private lateinit var appContext: Context

    private val appPreferences: AppPreferences by lazy {
        AppPreferences(appContext)
    }

    private val locationProvider: LocationProvider by lazy {
        LocationProvider(appContext)
    }

    private val okHttpClient: OkHttpClient by lazy {
        NetworkModule.provideOkHttpClient(
            appContext,
            ClientHeadersInterceptor(appPreferences, locationProvider)
        )
    }

    private val retrofit: Retrofit by lazy {
        NetworkModule.provideRetrofit(okHttpClient)
    }

    private val trackDatabase: TrackDatabase by lazy {
        Room.databaseBuilder(
            appContext,
            TrackDatabase::class.java,
            "track_db"
        ).fallbackToDestructiveMigration().build()
    }

    val trackRepository: TrackRepository by lazy {
        TrackRepository(retrofit, trackDatabase.trackPointDao())
    }

    val userRepository: UserRepository by lazy {
        UserRepository(retrofit, appPreferences)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository(appPreferences)
    }

    fun init(application: Application) {
        appContext = application.applicationContext
    }

    fun appPrefs(): AppPreferences = appPreferences
    fun location(): LocationProvider = locationProvider
}
