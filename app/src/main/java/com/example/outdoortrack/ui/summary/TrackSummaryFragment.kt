package com.example.outdoortrack.ui.summary

import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.PolylineOptions
import com.example.outdoortrack.core.ServiceLocator
import com.example.outdoortrack.databinding.FragmentTrackSummaryBinding

/**
 * 轨迹记录结束总结页：展示轨迹地图与信息，支持导出图片与上传云端。
 */
class TrackSummaryFragment : Fragment() {

    private var _binding: FragmentTrackSummaryBinding? = null
    private val binding get() = _binding!!

    private val args: TrackSummaryFragmentArgs by navArgs()

    private lateinit var viewModel: TrackSummaryViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            TrackSummaryViewModel.Factory(ServiceLocator.trackRepository)
        )[TrackSummaryViewModel::class.java]

        binding.mapView.onCreate(savedInstanceState)

        binding.btnExportImage.setOnClickListener {
            exportMapScreenshot()
        }

        binding.btnUploadCloud.setOnClickListener {
            viewModel.uploadToCloud(args.trackId) {
                val action = TrackSummaryFragmentDirections.actionTrackSummaryFragmentToUploadSuccessFragment()
                findNavController().navigate(action)
            }
        }

        observeState()
        viewModel.loadSummary(args.trackId)
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
    }

    private fun exportMapScreenshot() {
        val aMap: AMap = binding.mapView.map
        aMap.getMapScreenShot { bitmap: Bitmap? ->
            if (bitmap != null) {
                val uri = MediaStore.Images.Media.insertImage(
                    requireContext().contentResolver,
                    bitmap,
                    "track_${System.currentTimeMillis()}",
                    "轨迹截图"
                )
                if (uri != null) {
                    Toast.makeText(requireContext(), "已保存到相册", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "保存失败", Toast.LENGTH_SHORT).show()
                }
            }
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
