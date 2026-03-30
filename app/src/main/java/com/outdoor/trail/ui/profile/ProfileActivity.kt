package com.outdoor.trail.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.outdoor.trail.data.local.TokenManager
import com.outdoor.trail.databinding.ActivityProfileBinding
import com.outdoor.trail.ui.settings.SettingsActivity

/**
 * 个人中心页Activity
 * 展示用户个人信息、运动统计数据
 */
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
        observeViewModel()
        viewModel.loadUserDetail(TokenManager.getUserId())
    }

    private fun setupUI() {
        binding.ivBack.setOnClickListener { finish() }
        binding.ivSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun observeViewModel() {
        viewModel.userDetail.observe(this) { user ->
            binding.tvNickname.text = user.nickname
            binding.tvSignature.text = user.signature.ifEmpty { "这个人很懒，什么都没写" }
            binding.tvTotalDistance.text = formatDistance(user.totalDistance)
            binding.tvTotalDuration.text = formatDuration(user.totalDuration)
            binding.tvTrackCount.text = "${user.trackCount}"
            if (user.avatarUrl.isNotEmpty()) {
                Glide.with(this).load(user.avatarUrl).circleCrop().into(binding.ivAvatar)
            }
        }
        viewModel.error.observe(this) { msg ->
            msg?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        }
    }

    private fun formatDistance(m: Double) = if (m >= 1000) String.format("%.1f km", m / 1000) else String.format("%.0f m", m)
    private fun formatDuration(s: Long): String { val h = s/3600; return String.format("%dh", h) }
}
