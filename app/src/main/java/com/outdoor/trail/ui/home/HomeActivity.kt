package com.outdoor.trail.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.outdoor.trail.data.local.TokenManager
import com.outdoor.trail.databinding.ActivityHomeBinding
import com.outdoor.trail.ui.detail.TrackDetailActivity
import com.outdoor.trail.ui.profile.ProfileActivity
import com.outdoor.trail.ui.recording.RecordingActivity
import com.outdoor.trail.ui.search.SearchActivity

/**
 * 首页Activity
 * 展示推荐轨迹列表，支持开始记录/继续记录，底部导航到个人中心
 */
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: TrackListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        TokenManager.updateLastActive()
        setupUI()
        observeViewModel()
        viewModel.loadRecommendList(refresh = true)
        viewModel.checkRunningTrack()
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkRunningTrack()
    }

    private fun setupUI() {
        // 轨迹列表
        adapter = TrackListAdapter { trackItem ->
            val intent = Intent(this, TrackDetailActivity::class.java)
            intent.putExtra("track_id", trackItem.trackId)
            startActivity(intent)
        }
        binding.rvTracks.layoutManager = LinearLayoutManager(this)
        binding.rvTracks.adapter = adapter

        // 下拉刷新
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadRecommendList(refresh = true)
            viewModel.checkRunningTrack()
        }

        // 搜索按钮
        binding.ivSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        // 开始记录/正在记录按钮
        binding.btnStartRecord.setOnClickListener {
            val running = viewModel.runningTrack.value
            if (running?.hasRunning == true) {
                // 有正在进行的轨迹，跳转到记录中页面
                val intent = Intent(this, RecordingActivity::class.java)
                intent.putExtra("track_id", running.trackId)
                startActivity(intent)
            } else {
                // 无进行中轨迹，创建新轨迹
                viewModel.createTrack(116.4074, 39.9042, 50.0) // 使用当前定位
            }
        }

        // 长按结束记录
        binding.btnStartRecord.setOnLongClickListener {
            val running = viewModel.runningTrack.value
            if (running?.hasRunning == true) {
                // TODO: 调用结束轨迹接口并跳转到总结页
                Toast.makeText(this, "轨迹记录已结束", Toast.LENGTH_SHORT).show()
            }
            true
        }

        // 底部导航 - 个人中心
        binding.navProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun observeViewModel() {
        viewModel.trackList.observe(this) { tracks ->
            adapter.submitList(tracks)
            binding.swipeRefresh.isRefreshing = false
        }

        viewModel.runningTrack.observe(this) { running ->
            if (running?.hasRunning == true) {
                binding.btnStartRecord.text = "正在记录"
                binding.ivRecordingIndicator.visibility = View.VISIBLE
            } else {
                binding.btnStartRecord.text = "开始记录"
                binding.ivRecordingIndicator.visibility = View.GONE
            }
        }

        viewModel.newTrackId.observe(this) { trackId ->
            trackId?.let {
                val intent = Intent(this, RecordingActivity::class.java)
                intent.putExtra("track_id", it)
                startActivity(intent)
            }
        }

        viewModel.isLoading.observe(this) { loading ->
            binding.swipeRefresh.isRefreshing = loading
        }

        viewModel.error.observe(this) { msg ->
            msg?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        }
    }
}
