package com.outdoor.trail.service

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.outdoor.trail.R
import com.outdoor.trail.ui.recording.RecordingActivity

/**
 * 轨迹记录前台服务
 * 使用高德定位SDK持续获取GPS坐标，支持后台定位
 * 基于融合定位：GNSS + AGNSS + WiFi + 基站 + 传感器
 */
class TrackRecordService : Service() {

    companion object {
        const val CHANNEL_ID = "track_recording_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_START = "action_start"
        const val ACTION_PAUSE = "action_pause"
        const val ACTION_RESUME = "action_resume"
        const val ACTION_STOP = "action_stop"
        const val EXTRA_TRACK_ID = "track_id"
    }

    private var locationClient: AMapLocationClient? = null
    private var trackId: String = ""
    private var isPaused = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                trackId = intent.getStringExtra(EXTRA_TRACK_ID) ?: ""
                startForeground(NOTIFICATION_ID, buildNotification("轨迹记录中"))
                startLocationUpdates()
            }
            ACTION_PAUSE -> {
                isPaused = true
                locationClient?.stopLocation()
                updateNotification("轨迹已暂停")
            }
            ACTION_RESUME -> {
                isPaused = false
                locationClient?.startLocation()
                updateNotification("轨迹记录中")
            }
            ACTION_STOP -> {
                locationClient?.stopLocation()
                locationClient?.onDestroy()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_STICKY
    }

    /**
     * 启动高德融合定位
     * 采样间隔2秒，使用高精度模式（GNSS+网络+传感器）
     */
    private fun startLocationUpdates() {
        locationClient = AMapLocationClient(applicationContext)
        val option = AMapLocationClientOption().apply {
            locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy // 高精度模式
            interval = 2000 // 2秒采样间隔
            isNeedAddress = false
            isSensorEnable = true // 启用传感器辅助
            isGpsFirst = true // GPS优先
        }
        locationClient?.setLocationOption(option)
        locationClient?.setLocationListener { location ->
            if (location != null && !isPaused && location.errorCode == 0) {
                // 滤波：精度大于50m的点丢弃
                if (location.accuracy <= 50f) {
                    // TODO: 上报坐标到服务端，或本地缓存后批量上报
                    val lat = location.latitude
                    val lng = location.longitude
                    val alt = location.altitude
                    val speed = location.speed
                    val bearing = location.bearing
                    val accuracy = location.accuracy
                    // 通过EventBus或LocalBroadcast通知UI更新
                }
            }
        }
        locationClient?.startLocation()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "轨迹记录", NotificationManager.IMPORTANCE_LOW
            ).apply { description = "轨迹记录运行中" }
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(title: String): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, RecordingActivity::class.java).apply {
                putExtra("track_id", trackId)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText("点击返回轨迹页面")
            .setSmallIcon(R.drawable.ic_recording)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(title: String) {
        val notification = buildNotification(title)
        getSystemService(NotificationManager::class.java)?.notify(NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        locationClient?.stopLocation()
        locationClient?.onDestroy()
        super.onDestroy()
    }
}
