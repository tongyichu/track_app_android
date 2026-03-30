package com.outdoor.trail.ui.search

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.outdoor.trail.databinding.ActivitySearchBinding
import com.outdoor.trail.ui.detail.TrackDetailActivity
import com.outdoor.trail.ui.home.TrackListAdapter

/**
 * 轨迹搜索页Activity
 * 支持关键词搜索全平台公开轨迹
 */
class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var adapter: TrackListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.ivBack.setOnClickListener { finish() }

        adapter = TrackListAdapter { item ->
            val intent = Intent(this, TrackDetailActivity::class.java)
            intent.putExtra("track_id", item.trackId)
            startActivity(intent)
        }
        binding.rvResults.layoutManager = LinearLayoutManager(this)
        binding.rvResults.adapter = adapter

        // 搜索框输入监听（防抖延迟搜索）
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val keyword = s?.toString()?.trim() ?: ""
                if (keyword.length >= 2) {
                    viewModel.search(keyword)
                }
            }
        })

        binding.btnSearch.setOnClickListener {
            val keyword = binding.etSearch.text.toString().trim()
            if (keyword.isNotEmpty()) {
                viewModel.search(keyword)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.searchResults.observe(this) { adapter.submitList(it) }
        viewModel.error.observe(this) { msg ->
            msg?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        }
    }
}
