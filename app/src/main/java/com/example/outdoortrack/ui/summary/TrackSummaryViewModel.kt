package com.example.outdoortrack.ui.summary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.outdoortrack.data.model.TrackMapResponse
import com.example.outdoortrack.data.model.TrackSummaryResponse
import com.example.outdoortrack.data.repository.TrackRepository
import kotlinx.coroutines.launch

class TrackSummaryViewModel(
    private val trackRepository: TrackRepository
) : ViewModel() {

    private val _summary = MutableLiveData<TrackSummaryResponse>()
    val summary: LiveData<TrackSummaryResponse> = _summary

    private val _mapData = MutableLiveData<TrackMapResponse>()
    val mapData: LiveData<TrackMapResponse> = _mapData

    fun loadSummary(trackId: String) {
        viewModelScope.launch {
            _summary.value = trackRepository.getTrackSummary(trackId)
            _mapData.value = trackRepository.getTrackMap(trackId)
        }
    }

    fun uploadToCloud(trackId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            trackRepository.uploadTrack(trackId)
            onSuccess()
        }
    }

    class Factory(
        private val trackRepository: TrackRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return TrackSummaryViewModel(trackRepository) as T
        }
    }
}
