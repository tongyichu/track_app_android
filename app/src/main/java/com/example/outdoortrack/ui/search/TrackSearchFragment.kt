package com.example.outdoortrack.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.outdoortrack.core.ServiceLocator
import com.example.outdoortrack.databinding.FragmentTrackSearchBinding
import com.example.outdoortrack.ui.home.adapter.TrackListAdapter

/**
 * 轨迹搜索页面：调用 /api/track/search/list 展示结果。
 */
class TrackSearchFragment : Fragment() {

    private var _binding: FragmentTrackSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: TrackSearchViewModel
    private lateinit var adapter: TrackListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            TrackSearchViewModel.Factory(ServiceLocator.trackRepository)
        )[TrackSearchViewModel::class.java]

        adapter = TrackListAdapter { /* 复用他人轨迹详情入口，可按需导航 */ }
        binding.recyclerSearch.adapter = adapter

        binding.editKeyword.addTextChangedListener { text ->
            viewModel.search(text?.toString())
        }

        viewModel.result.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
