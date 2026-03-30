package com.outdoor.trail.ui.detail

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.PolylineOptions
import android.graphics.Color
import com.bumptech.glide.Glide
import com.outdoor.trail.data.local.TokenManager
import com.outdoor.trail.databinding.ActivityTrackDetailBinding
import com.outdoor.trail.ui.recording.RecordingActivity

/**
 * 他人轨迹详情页Activity
 * 展示轨迹地图、扼要信息和收藏状态
 * 支持"使用轨迹导航"和"收藏/取消收藏"操作
 */
class TrackDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrackDetailBinding
    private val viewModel: TrackDetailViewModel by viewModels()
    private lateinit var aMap: AMap
    private var trackId: String = ""
    private var isCollected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackDetailBinding.inflate(layoutInflater)
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

        // 使用轨迹导航
        binding.btnNavigate.setOnClickListener {
            viewModel.startNavigation(trackId, TokenManager.getUserId())
        }

        // 收藏/取消收藏
        binding.btnCollect.setOnClickListener {
            if (isCollected) {
                viewModel.uncollectTrack(trackId, TokenManager.getUserId())
            } else {
                viewModel.collectTrack(trackId, TokenManager.getUserId())
            }
        }
    }

    private fun observeViewModel() {
        viewModel.trackMap.observe(this) { mapData ->
            if (mapData.points.isNotEmpty()) {
                val opts = PolylineOptions().width(8f).color(Color.parseColor("#FF4081"))
                mapData.points.forEach { opts.add(LatLng(it.latitude, it.longitude)) }
                aMap.addPolyline(opts)
                aMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(LatLng(mapData.centerLat, mapData.centerLng), 14f)
                )
            }
        }

        viewModel.trackSummary.observe(this) { summary ->
            binding.tvTitle.text = summary.title
            binding.tvDistance.text = formatDistance(summary.distance)
            binding.tvDuration.text = formatDuration(summary.duration)
            binding.tvElevation.text = String.format("↑%.0fm", summary.elevationGain)
            binding.tvRegion.text = summary.region
            binding.tvUserName.text = summary.userNickname
            binding.tvCollectCount.text = "${summary.collectCount} 收藏"
            if (summary.userAvatar.isNotEmpty()) {
                Glide.with(this).load(summary.userAvatar).circleCrop().into(binding.ivUserAvatar)
            }
        }

        viewModel.collectStatus.observe(this) { status ->
            isCollected = status.isCollected
            binding.btnCollect.text = if (isCollected) "已收藏" else "收藏轨迹"
        }

        viewModel.navigateToRecording.observe(this) { newTrackId ->
            newTrackId?.let {
                val intent = Intent(this, RecordingActivity::class.java)
                intent.putExtra("track_id", it)
                startActivity(intent)
            }
        }

        viewModel.error.observe(this) { msg ->
            msg?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        }
    }

    private fun loadData() {
        viewModel.loadTrackMap(trackId)
        viewModel.loadTrackSummary(trackId)
        viewModel.checkCollectStatus(TokenManager.getUserId(), trackId)
    }

    private fun formatDistance(m: Double) = if (m >= 1000) String.format("%.1f km", m / 1000) else String.format("%.0f m", m)
    private fun formatDuration(s: Long): String { val h = s/3600; val m = (s%3600)/60; return if (h > 0) String.format("%dh%02dm", h, m) else String.format("%dm", m) }

    override fun onResume() { super.onResume(); binding.mapView.onResume() }
    override fun onPause() { super.onPause(); binding.mapView.onPause() }
    override fun onDestroy() { super.onDestroy(); binding.mapView.onDestroy() }
    override fun onSaveInstanceState(o: Bundle) { super.onSaveInstanceState(o); binding.mapView.onSaveInstanceState(o) }
}
