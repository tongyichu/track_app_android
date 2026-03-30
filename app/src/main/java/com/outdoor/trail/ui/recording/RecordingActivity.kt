package com.outdoor.trail.ui.recording

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.PolylineOptions
import com.outdoor.trail.R
import com.outdoor.trail.databinding.ActivityRecordingBinding
import com.outdoor.trail.ui.summary.TrackSummaryActivity
import android.content.Intent
import android.graphics.Color

/**
 * 正在记录页Activity
 * 展示实时轨迹地图和运动数据，支持暂停/继续/结束操作
 * 点击地图可全屏展示，支持缩放和拖拽
 */
class RecordingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecordingBinding
    private val viewModel: RecordingViewModel by viewModels()
    private lateinit var aMap: AMap
    private var trackId: String = ""
    private var isPaused = false
    private var isMapExpanded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        trackId = intent.getStringExtra("track_id") ?: ""
        binding.mapView.onCreate(savedInstanceState)
        aMap = binding.mapView.map

        setupUI()
        observeViewModel()
        loadTrackData()
    }

    private fun setupUI() {
        // 返回按钮
        binding.ivBack.setOnClickListener { finish() }

        // 暂停/继续按钮
        binding.btnPauseResume.setOnClickListener {
            isPaused = !isPaused
            if (isPaused) {
                binding.btnPauseResume.text = "继续"
                binding.btnPauseResume.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_play, 0, 0, 0
                )
            } else {
                binding.btnPauseResume.text = "暂停"
                binding.btnPauseResume.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_pause, 0, 0, 0
                )
            }
        }

        // 长按暂停按钮 -> 结束记录
        binding.btnPauseResume.setOnLongClickListener {
            viewModel.finishTrack(trackId)
            true
        }

        // 点击地图区域切换全屏/普通模式
        binding.mapView.setOnClickListener {
            isMapExpanded = !isMapExpanded
            if (isMapExpanded) {
                binding.layoutInfo.visibility = View.GONE
                binding.btnPauseResume.visibility = View.GONE
            } else {
                binding.layoutInfo.visibility = View.VISIBLE
                binding.btnPauseResume.visibility = View.VISIBLE
            }
        }
    }

    private fun observeViewModel() {
        viewModel.trackMap.observe(this) { mapData ->
            if (mapData.points.isNotEmpty()) {
                // 绘制轨迹线
                val polylineOptions = PolylineOptions()
                    .width(8f)
                    .color(Color.parseColor("#FF4081"))
                mapData.points.forEach { point ->
                    polylineOptions.add(LatLng(point.latitude, point.longitude))
                }
                aMap.addPolyline(polylineOptions)

                // 移动相机到中心点
                aMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(mapData.centerLat, mapData.centerLng), 15f
                    )
                )
            }
        }

        viewModel.trackDetail.observe(this) { detail ->
            binding.tvDistance.text = formatDistance(detail.distance)
            binding.tvDuration.text = formatDuration(detail.duration)
            binding.tvAvgSpeed.text = String.format("%.1f km/h", detail.avgSpeed * 3.6)
            binding.tvElevation.text = String.format("↑%.0fm ↓%.0fm", detail.elevationGain, detail.elevationLoss)
            binding.tvMaxAltitude.text = String.format("%.0f m", detail.maxAltitude)
        }

        viewModel.trackFinished.observe(this) { finished ->
            if (finished) {
                val intent = Intent(this, TrackSummaryActivity::class.java)
                intent.putExtra("track_id", trackId)
                startActivity(intent)
                finish()
            }
        }

        viewModel.error.observe(this) { msg ->
            msg?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        }
    }

    private fun loadTrackData() {
        if (trackId.isNotEmpty()) {
            viewModel.loadTrackMap(trackId)
            viewModel.loadTrackDetail(trackId)
        }
    }

    private fun formatDistance(meters: Double): String {
        return if (meters >= 1000) String.format("%.2f km", meters / 1000)
        else String.format("%.0f m", meters)
    }

    private fun formatDuration(seconds: Long): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return String.format("%02d:%02d:%02d", h, m, s)
    }

    override fun onResume() { super.onResume(); binding.mapView.onResume() }
    override fun onPause() { super.onPause(); binding.mapView.onPause() }
    override fun onDestroy() { super.onDestroy(); binding.mapView.onDestroy() }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState); binding.mapView.onSaveInstanceState(outState)
    }
}
