package com.example.outdoortrack.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.outdoortrack.data.model.TrackMapResponse
import com.example.outdoortrack.data.model.TrackSummaryResponse
import com.example.outdoortrack.data.repository.TrackRepository
import com.example.outdoortrack.data.repository.UserRepository
import kotlinx.coroutines.launch

class TrackDetailViewModel(
    private val trackRepository: TrackRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private lateinit var trackId: String
    private var userId: String? = null

    private val _summary = MutableLiveData<TrackSummaryResponse>()
    val summary: LiveData<TrackSummaryResponse> = _summary

    private val _mapData = MutableLiveData<TrackMapResponse>()
    val mapData: LiveData<TrackMapResponse> = _mapData

    private val _collected = MutableLiveData(false)
    val collected: LiveData<Boolean> = _collected

    fun load(trackId: String) {
        this.trackId = trackId
        userId = userRepository.currentUserId()
        viewModelScope.launch {
            val uid = userId ?: return@launch
            _summary.value = trackRepository.getTrackSummary(trackId)
            _mapData.value = trackRepository.getTrackMap(trackId)
            _collected.value = trackRepository.isCollected(uid, trackId)
        }
    }

    fun toggleCollect() {
        val uid = userId ?: return
        viewModelScope.launch {
            if (_collected.value == true) {
                trackRepository.unCollectTrack(uid, trackId)
                _collected.value = false
            } else {
                trackRepository.collectTrack(uid, trackId)
                _collected.value = true
            }
        }
    }

    fun useForNavigation(onCreated: () -> Unit) {
        val uid = userId ?: return
        viewModelScope.launch {
            val running = trackRepository.getRunningTrack(uid)
            if (running != null) {
                // 已有正在进行中的轨迹，仅提示
                onCreated()
                return@launch
            }
            val created = trackRepository.createTrack()
            onCreated()
        }
    }

    class Factory(
        private val trackRepository: TrackRepository,
        private val userRepository: UserRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return TrackDetailViewModel(trackRepository, userRepository) as T
        }
    }
}
