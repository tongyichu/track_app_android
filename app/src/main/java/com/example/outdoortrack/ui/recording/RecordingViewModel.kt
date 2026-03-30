package com.example.outdoortrack.ui.recording

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.outdoortrack.data.model.TrackDetailResponse
import com.example.outdoortrack.data.model.TrackMapResponse
import com.example.outdoortrack.data.repository.TrackRepository
import kotlinx.coroutines.launch

class RecordingViewModel(
    private val trackRepository: TrackRepository
) : ViewModel() {

    private val _trackDetail = MutableLiveData<TrackDetailResponse>()
    val trackDetail: LiveData<TrackDetailResponse> = _trackDetail

    private val _mapData = MutableLiveData<TrackMapResponse>()
    val mapData: LiveData<TrackMapResponse> = _mapData

    private val _isPaused = MutableLiveData(false)
    val isPaused: LiveData<Boolean> = _isPaused

    fun loadTrack(trackId: String) {
        viewModelScope.launch {
            _trackDetail.value = trackRepository.getTrackSummary(trackId).let {
                TrackDetailResponse(
                    id = it.id,
                    name = it.name,
                    distanceMeters = it.distanceMeters,
                    durationSeconds = it.durationSeconds,
                    elevationGain = it.elevationGain,
                    avgPace = null,
                    status = "running"
                )
            }
            _mapData.value = trackRepository.getTrackMap(trackId)
        }
    }

    fun togglePause() {
        _isPaused.value = !(_isPaused.value ?: false)
    }

    class Factory(
        private val trackRepository: TrackRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return RecordingViewModel(trackRepository) as T
        }
    }
}
