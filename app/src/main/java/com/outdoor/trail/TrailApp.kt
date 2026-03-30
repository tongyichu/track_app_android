package com.outdoor.trail

import android.app.Application
import com.outdoor.trail.data.local.TokenManager

/**
 * 应用全局Application类
 * 负责初始化高德地图SDK、TokenManager等全局组件
 */
class TrailApp : Application() {

    companion object {
        lateinit var instance: TrailApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        TokenManager.init(this)
    }
}
