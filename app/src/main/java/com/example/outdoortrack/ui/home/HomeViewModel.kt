package com.example.outdoortrack.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.outdoortrack.data.model.TrackListItem
import com.example.outdoortrack.data.repository.TrackRepository
import com.example.outdoortrack.data.repository.UserRepository
import kotlinx.coroutines.launch

/**
 * 首页 ViewModel：负责推荐列表、未结束轨迹状态等。
 */
class HomeViewModel(
    private val trackRepository: TrackRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    data class HomeState(
        val recommendTracks: List<TrackListItem> = emptyList(),
        val hasRunningTrack: Boolean = false,
        val runningTrackId: String? = null
    )

    private val _homeState = MutableLiveData(HomeState())
    val homeState: LiveData<HomeState> = _homeState

    fun loadHomeData() {
        viewModelScope.launch {
            val userId = userRepository.currentUserId().orEmpty()
            val recommend = trackRepository.getRecommendList()
            val running = if (userId.isNotBlank()) trackRepository.getRunningTrack(userId) else null
            _homeState.value = HomeState(
                recommendTracks = recommend,
                hasRunningTrack = running != null,
                runningTrackId = running?.id
            )
        }
    }

    fun startNewTrack(onCreated: (String) -> Unit) {
        viewModelScope.launch {
            val detail = trackRepository.createTrack()
            _homeState.value = _homeState.value?.copy(
                hasRunningTrack = true,
                runningTrackId = detail.id
            )
            onCreated(detail.id)
        }
    }

    class Factory(
        private val trackRepository: TrackRepository,
        private val userRepository: UserRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(trackRepository, userRepository) as T
        }
    }
}
