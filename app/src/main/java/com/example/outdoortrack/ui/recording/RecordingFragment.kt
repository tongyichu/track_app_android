package com.example.outdoortrack.ui.recording

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.PolylineOptions
import com.example.outdoortrack.core.ServiceLocator
import com.example.outdoortrack.databinding.FragmentRecordingBinding

/**
 * 正在记录详情页：展示实时地图与轨迹信息，可暂停/继续/长按结束。
 */
class RecordingFragment : Fragment() {

    private var _binding: FragmentRecordingBinding? = null
    private val binding get() = _binding!!

    private val args: RecordingFragmentArgs by navArgs()

    private lateinit var viewModel: RecordingViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecordingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            RecordingViewModel.Factory(ServiceLocator.trackRepository)
        )[RecordingViewModel::class.java]

        binding.mapView.onCreate(savedInstanceState)

        binding.btnPauseResume.setOnClickListener {
            viewModel.togglePause()
        }

        binding.btnPauseResume.setOnLongClickListener {
            // 长按结束
            Toast.makeText(requireContext(), "结束记录", Toast.LENGTH_SHORT).show()
            val action = RecordingFragmentDirections.actionRecordingFragmentToTrackSummaryFragment(args.trackId)
            findNavController().navigate(action)
            true
        }

        binding.mapView.map.setOnMapClickListener {
            // 点击地图放大占位
            binding.mapView.map.animateCamera(CameraUpdateFactory.zoomIn())
        }

        observeState()
        viewModel.loadTrack(args.trackId)
    }

    private fun observeState() {
        viewModel.trackDetail.observe(viewLifecycleOwner) { detail ->
            binding.tvInfo.text = "距离：${detail.distanceMeters ?: 0.0} m"
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
        viewModel.isPaused.observe(viewLifecycleOwner) { paused ->
            binding.btnPauseResume.text = if (paused) "继续" else "暂停"
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
