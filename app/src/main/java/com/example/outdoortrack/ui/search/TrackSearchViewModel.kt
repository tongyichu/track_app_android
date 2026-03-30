package com.example.outdoortrack.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.outdoortrack.data.model.TrackListItem
import com.example.outdoortrack.data.repository.TrackRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TrackSearchViewModel(
    private val trackRepository: TrackRepository
) : ViewModel() {

    private val _result = MutableLiveData<List<TrackListItem>>(emptyList())
    val result: LiveData<List<TrackListItem>> = _result

    private var searchJob: Job? = null

    fun search(keyword: String?) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // 简单防抖
            val list = trackRepository.searchTracks(keyword)
            _result.value = list
        }
    }

    class Factory(
        private val trackRepository: TrackRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return TrackSearchViewModel(trackRepository) as T
        }
    }
}
