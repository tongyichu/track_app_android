package com.example.outdoortrack.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.outdoortrack.R
import com.example.outdoortrack.core.ServiceLocator
import com.example.outdoortrack.databinding.FragmentHomeBinding
import com.example.outdoortrack.ui.home.adapter.TrackListAdapter

/**
 * 首页 / 首页-正在记录：根据是否存在未结束的轨迹展示不同状态。
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: TrackListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            HomeViewModel.Factory(ServiceLocator.trackRepository, ServiceLocator.userRepository)
        )[HomeViewModel::class.java]

        adapter = TrackListAdapter { track ->
            val action = HomeFragmentDirections.actionHomeFragmentToTrackDetailFragment(track.id)
            findNavController().navigate(action)
        }
        binding.recyclerTracks.adapter = adapter

        binding.btnSearch.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_trackSearchFragment)
        }

        binding.btnStartOrRunning.setOnClickListener {
            val state = viewModel.homeState.value
            if (state?.hasRunningTrack == true) {
                // 进入“正在记录详情”
                val action = HomeFragmentDirections.actionHomeFragmentToRecordingFragment(state.runningTrackId!!)
                findNavController().navigate(action)
            } else {
                viewModel.startNewTrack { trackId ->
                    val action = HomeFragmentDirections.actionHomeFragmentToRecordingFragment(trackId)
                    findNavController().navigate(action)
                }
            }
        }

        binding.btnStartOrRunning.setOnLongClickListener {
            // 长按结束当前轨迹并跳转结束总结
            val state = viewModel.homeState.value
            if (state?.hasRunningTrack == true) {
                val action = HomeFragmentDirections.actionHomeFragmentToTrackSummaryFragment(state.runningTrackId!!)
                findNavController().navigate(action)
            }
            true
        }

        binding.btnProfile.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }

        observeState()
        viewModel.loadHomeData()
    }

    private fun observeState() {
        viewModel.homeState.observe(viewLifecycleOwner) { state ->
            binding.tvTitle.text = if (state.hasRunningTrack) "首页 - 正在记录" else "首页"
            binding.btnStartOrRunning.text = if (state.hasRunningTrack) "正在记录" else "开始记录"
            adapter.submitList(state.recommendTracks)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
