package com.example.outdoortrack.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.PolylineOptions
import com.example.outdoortrack.core.ServiceLocator
import com.example.outdoortrack.databinding.FragmentTrackDetailBinding

/**
 * 他人轨迹详情页：展示轨迹地图、扼要信息、收藏状态与“使用轨迹导航”。
 */
class TrackDetailFragment : Fragment() {

    private var _binding: FragmentTrackDetailBinding? = null
    private val binding get() = _binding!!

    private val args: TrackDetailFragmentArgs by navArgs()

    private lateinit var viewModel: TrackDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            TrackDetailViewModel.Factory(ServiceLocator.trackRepository, ServiceLocator.userRepository)
        )[TrackDetailViewModel::class.java]

        binding.mapView.onCreate(savedInstanceState)

        binding.btnToggleCollect.setOnClickListener {
            viewModel.toggleCollect()
        }

        binding.btnUseForNavigation.setOnClickListener {
            viewModel.useForNavigation {
                Toast.makeText(requireContext(), "已为当前用户创建新的导航记录", Toast.LENGTH_SHORT).show()
            }
        }

        observeState()
        viewModel.load(args.trackId)
    }

    private fun observeState() {
        viewModel.summary.observe(viewLifecycleOwner) { summary ->
            binding.tvInfo.text = "距离：${summary.distanceMeters ?: 0.0} m"
        }
        viewModel.mapData.observe(viewLifecycleOwner) { map ->
            val aMap = binding.mapView.map
            aMap.clear()
            val options = PolylineOptions()
            map.points.forEach { p ->
                options.add(LatLng(p.latitude, p.longitude))
            }
            aMap.addPolyline(options)
            if (map.points.isNotEmpty()) {
                val first = map.points.first()
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(first.latitude, first.longitude), 14f))
            }
        }
        viewModel.collected.observe(viewLifecycleOwner) { collected ->
            binding.btnToggleCollect.text = if (collected) "已收藏" else "收藏轨迹"
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onDestroy()
        _binding = null
    }
}
