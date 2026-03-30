package com.outdoor.trail.ui.summary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.outdoor.trail.data.model.TrackDetailResponse
import com.outdoor.trail.data.model.TrackMapResponse
import com.outdoor.trail.data.repository.TrackRepository
import kotlinx.coroutines.launch

/**
 * 轨迹结束总结页ViewModel
 */
class TrackSummaryViewModel : ViewModel() {

    private val repository = TrackRepository()

    private val _trackMap = MutableLiveData<TrackMapResponse>()
    val trackMap: LiveData<TrackMapResponse> = _trackMap

    private val _trackDetail = MutableLiveData<TrackDetailResponse>()
    val trackDetail: LiveData<TrackDetailResponse> = _trackDetail

    private val _uploadSuccess = MutableLiveData<Boolean>()
    val uploadSuccess: LiveData<Boolean> = _uploadSuccess

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

    fun loadTrackDetail(trackId: String) {
        viewModelScope.launch {
            repository.getTrackDetail(trackId).fold(
                onSuccess = { _trackDetail.value = it },
                onFailure = { _error.value = it.message }
            )
        }
    }

    /** 上传轨迹到云端 */
    fun uploadToCloud(trackId: String) {
        viewModelScope.launch {
            repository.uploadToCloud(trackId).fold(
                onSuccess = { _uploadSuccess.value = true },
                onFailure = { _error.value = it.message }
            )
        }
    }
}
