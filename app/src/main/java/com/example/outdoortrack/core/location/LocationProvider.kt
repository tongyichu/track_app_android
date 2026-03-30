package com.example.outdoortrack.core.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * 简单的定位提供者，占位集成高德定位 SDK。
 *
 * 真实项目中应补充运行时权限处理、生命周期绑定等。
 */
class LocationProvider(context: Context) {

    private val _lastLocation = MutableStateFlow<Location?>(null)
    val lastLocation: StateFlow<Location?> = _lastLocation

    private var locationClient: AMapLocationClient? = null

    init {
        // 仅作为占位示例，不做异常兜底处理
        try {
            locationClient = AMapLocationClient(context.applicationContext).apply {
                val option = AMapLocationClientOption().apply {
                    locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
                    interval = 5_000L
                }
                setLocationOption(option)
                setLocationListener { aMapLocation ->
                    if (aMapLocation != null && aMapLocation.errorCode == AMapLocation.LOCATION_SUCCESS) {
                        _lastLocation.value = aMapLocation.toLocation()
                    }
                }
            }
        } catch (e: Exception) {
            // 占位实现，如 SDK 不可用则保持 lastLocation 为空
        }
    }

    @SuppressLint("MissingPermission")
    fun start() {
        locationClient?.startLocation()
    }

    fun stop() {
        locationClient?.stopLocation()
    }

    fun destroy() {
        locationClient?.onDestroy()
    }

    /**
     * 将 AMapLocation 转为系统 Location 以便统一处理。
     */
    private fun AMapLocation.toLocation(): Location = Location("amap").apply {
        latitude = this@toLocation.latitude
        longitude = this@toLocation.longitude
        accuracy = this@toLocation.accuracy
        altitude = this@toLocation.altitude
        time = this@toLocation.time
    }

    /**
     * 返回 header 需要的 "lat,lng" 字符串，占位实现。
     */
    fun geoHeaderString(): String? {
        val loc = _lastLocation.value ?: return null
        return "${loc.latitude},${loc.longitude}"
    }
}
