package com.outdoor.trail.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.outdoor.trail.data.model.*
import com.outdoor.trail.data.repository.TrackRepository
import kotlinx.coroutines.launch

/**
 * 他人轨迹详情页ViewModel
 * 管理轨迹地图、摘要、收藏状态和导航创建逻辑
 */
class TrackDetailViewModel : ViewModel() {

    private val repository = TrackRepository()

    private val _trackMap = MutableLiveData<TrackMapResponse>()
    val trackMap: LiveData<TrackMapResponse> = _trackMap

    private val _trackSummary = MutableLiveData<TrackSummaryResponse>()
    val trackSummary: LiveData<TrackSummaryResponse> = _trackSummary

    private val _collectStatus = MutableLiveData<CollectStatusResponse>()
    val collectStatus: LiveData<CollectStatusResponse> = _collectStatus

    private val _navigateToRecording = MutableLiveData<String?>()
    val navigateToRecording: LiveData<String?> = _navigateToRecording

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadTrackMap(trackId: String) {
        viewModelScope.launch {
            repository.getTrackMap(trackId).fold(
                onSuccess = { _trackMap.value = it },
                onFailure = { _error.value = it.message }
            )
        }
    }

    fun loadTrackSummary(trackId: String) {
        viewModelScope.launch {
            repository.getTrackSummary(trackId).fold(
                onSuccess = { _trackSummary.value = it },
                onFailure = { _error.value = it.message }
            )
        }
    }

    fun checkCollectStatus(userId: String, trackId: String) {
        viewModelScope.launch {
            repository.getCollectStatus(userId, trackId).fold(
                onSuccess = { _collectStatus.value = it },
                onFailure = { /* 默认未收藏 */ }
            )
        }
    }

    /** 收藏轨迹 */
    fun collectTrack(trackId: String, userId: String) {
        viewModelScope.launch {
            repository.collectTrack(trackId, userId).fold(
                onSuccess = { _collectStatus.value = CollectStatusResponse(isCollected = true) },
                onFailure = { _error.value = it.message }
            )
        }
    }

    /** 取消收藏 */
    fun uncollectTrack(trackId: String, userId: String) {
        viewModelScope.launch {
            repository.uncollectTrack(trackId, userId).fold(
                onSuccess = { _collectStatus.value = CollectStatusResponse(isCollected = false) },
                onFailure = { _error.value = it.message }
            )
        }
    }

    /**
     * 使用轨迹导航：
     * 1. 先检查是否有正在进行中的轨迹
     * 2. 如果有，提示用户
     * 3. 如果没有，创建新轨迹并跳转到记录页
     */
    fun startNavigation(trackId: String, userId: String) {
        viewModelScope.launch {
            repository.getRunningTrack(userId).fold(
                onSuccess = { running ->
                    if (running.hasRunning) {
                        _error.value = "您有正在进行中的轨迹，请先结束当前轨迹"
                    } else {
                        // 创建新轨迹
                        val request = CreateTrackRequest(
                            sportType = "hiking", title = "导航轨迹",
                            longitude = 0.0, latitude = 0.0, altitude = 0.0
                        )
                        repository.createTrack(request).fold(
                            onSuccess = { _navigateToRecording.value = it.trackId },
                            onFailure = { _error.value = it.message }
                        )
                    }
                },
                onFailure = { _error.value = it.message }
            )
        }
    }
}
