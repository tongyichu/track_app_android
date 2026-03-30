package com.outdoor.trail.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.outdoor.trail.data.local.TokenManager
import com.outdoor.trail.data.model.*
import com.outdoor.trail.data.repository.TrackRepository
import kotlinx.coroutines.launch

/**
 * 首页ViewModel，管理推荐轨迹列表和正在进行中的轨迹状态
 */
class HomeViewModel : ViewModel() {

    private val repository = TrackRepository()

    /** 推荐轨迹列表 */
    private val _trackList = MutableLiveData<List<TrackListItem>>()
    val trackList: LiveData<List<TrackListItem>> = _trackList

    /** 正在进行中的轨迹 */
    private val _runningTrack = MutableLiveData<RunningTrackResponse?>()
    val runningTrack: LiveData<RunningTrackResponse?> = _runningTrack

    /** 新创建的轨迹ID */
    private val _newTrackId = MutableLiveData<String?>()
    val newTrackId: LiveData<String?> = _newTrackId

    /** 加载状态 */
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    /** 错误信息 */
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private var currentPage = 1

    /** 加载推荐轨迹列表 */
    fun loadRecommendList(refresh: Boolean = false) {
        if (refresh) currentPage = 1
        _isLoading.value = true

        viewModelScope.launch {
            repository.getRecommendList(currentPage, 20).fold(
                onSuccess = { response ->
                    if (refresh) {
                        _trackList.value = response.tracks
                    } else {
                        val current = _trackList.value.orEmpty().toMutableList()
                        current.addAll(response.tracks)
                        _trackList.value = current
                    }
                    _isLoading.value = false
                },
                onFailure = { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
            )
        }
    }

    /** 检查是否有正在进行中的轨迹 */
    fun checkRunningTrack() {
        viewModelScope.launch {
            repository.getRunningTrack(TokenManager.getUserId()).fold(
                onSuccess = { _runningTrack.value = it },
                onFailure = { _runningTrack.value = null }
            )
        }
    }

    /** 创建新轨迹记录 */
    fun createTrack(longitude: Double, latitude: Double, altitude: Double) {
        viewModelScope.launch {
            val request = CreateTrackRequest(
                sportType = "hiking",
                title = "轨迹记录",
                longitude = longitude,
                latitude = latitude,
                altitude = altitude
            )
            repository.createTrack(request).fold(
                onSuccess = { _newTrackId.value = it.trackId },
                onFailure = { _error.value = it.message }
            )
        }
    }

    /** 加载更多 */
    fun loadMore() {
        currentPage++
        loadRecommendList(false)
    }
}
