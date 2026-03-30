package com.outdoor.trail.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.outdoor.trail.data.model.TrackListItem
import com.outdoor.trail.data.repository.TrackRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 搜索页ViewModel，支持防抖搜索
 */
class SearchViewModel : ViewModel() {

    private val repository = TrackRepository()
    private var searchJob: Job? = null

    private val _searchResults = MutableLiveData<List<TrackListItem>>()
    val searchResults: LiveData<List<TrackListItem>> = _searchResults

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    /** 执行搜索（带300ms防抖） */
    fun search(keyword: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // 防抖
            repository.searchTracks(keyword, 1, 20).fold(
                onSuccess = { _searchResults.value = it.tracks },
                onFailure = { _error.value = it.message }
            )
        }
    }
}
