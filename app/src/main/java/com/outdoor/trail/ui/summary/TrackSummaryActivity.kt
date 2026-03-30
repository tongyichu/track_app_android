package com.outdoor.trail.ui.summary

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.PolylineOptions
import android.graphics.Color
import com.outdoor.trail.databinding.ActivityTrackSummaryBinding
import com.outdoor.trail.ui.feedback.UploadSuccessActivity

/**
 * 轨迹记录结束总结页Activity
 * 展示完整轨迹地图和运动数据统计
 * 支持导出轨迹图片到相册和上传到云端
 */
class TrackSummaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrackSummaryBinding
    private val viewModel: TrackSummaryViewModel by viewModels()
    private lateinit var aMap: AMap
    private var trackId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        trackId = intent.getStringExtra("track_id") ?: ""
        binding.mapView.onCreate(savedInstanceState)
        aMap = binding.mapView.map

        setupUI()
        observeViewModel()
        loadData()
    }

    private fun setupUI() {
        binding.ivBack.setOnClickListener { finish() }

        // 导出轨迹图片
        binding.btnExportImage.setOnClickListener {
            aMap.getMapScreenShot(object : AMap.OnMapScreenShotListener {
                override fun onMapScreenShot(bitmap: Bitmap?) {
                    bitmap?.let {
                        MediaStore.Images.Media.insertImage(
                            contentResolver, it, "track_$trackId", "户外轨迹"
                        )
                        Toast.makeText(this@TrackSummaryActivity, "已保存到相册", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onMapScreenShot(bitmap: Bitmap?, status: Int) {}
            })
        }

        // 上传到云端
        binding.btnUploadCloud.setOnClickListener {
            viewModel.uploadToCloud(trackId)
        }
    }

    private fun observeViewModel() {
        viewModel.trackMap.observe(this) { mapData ->
            if (mapData.points.isNotEmpty()) {
                val polylineOptions = PolylineOptions().width(8f).color(Color.parseColor("#FF4081"))
                mapData.points.forEach { p ->
                    polylineOptions.add(LatLng(p.latitude, p.longitude))
                }
                aMap.addPolyline(polylineOptions)
                aMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(mapData.centerLat, mapData.centerLng), 14f
                    )
                )
            }
        }

        viewModel.trackDetail.observe(this) { detail ->
            binding.tvTitle.text = detail.title
            binding.tvDistance.text = formatDistance(detail.distance)
            binding.tvDuration.text = formatDuration(detail.duration)
            binding.tvAvgSpeed.text = String.format("%.1f km/h", detail.avgSpeed * 3.6)
            binding.tvElevationGain.text = String.format("%.0f m", detail.elevationGain)
            binding.tvElevationLoss.text = String.format("%.0f m", detail.elevationLoss)
            binding.tvMaxAltitude.text = String.format("%.0f m", detail.maxAltitude)
        }

        viewModel.uploadSuccess.observe(this) { success ->
            if (success) {
                val intent = Intent(this, UploadSuccessActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        viewModel.error.observe(this) { msg ->
            msg?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        }
    }

    private fun loadData() {
        viewModel.loadTrackMap(trackId)
        viewModel.loadTrackDetail(trackId)
    }

    private fun formatDistance(meters: Double): String =
        if (meters >= 1000) String.format("%.2f km", meters / 1000) else String.format("%.0f m", meters)

    private fun formatDuration(seconds: Long): String {
        val h = seconds / 3600; val m = (seconds % 3600) / 60; val s = seconds % 60
        return String.format("%02d:%02d:%02d", h, m, s)
    }

    override fun onResume() { super.onResume(); binding.mapView.onResume() }
    override fun onPause() { super.onPause(); binding.mapView.onPause() }
    override fun onDestroy() { super.onDestroy(); binding.mapView.onDestroy() }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState); binding.mapView.onSaveInstanceState(outState)
    }
}
