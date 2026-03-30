package com.example.outdoortrack

import android.app.Application
import com.example.outdoortrack.core.ServiceLocator

/**
 * 应用入口 Application，用于初始化全局单例（网络、数据库、定位等）。
 */
class OutdoorTrackApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
    }
}
