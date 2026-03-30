package com.example.outdoortrack.ui.settings

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.outdoortrack.core.ServiceLocator
import com.example.outdoortrack.databinding.FragmentSettingsBinding

/**
 * 设置页：头像、用户名、签名、语言、退出登录。
 * 微信、相册等能力仅做最小可跑占位实现。
 */
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SettingsViewModel

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.updateAvatar(requireContext(), it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            SettingsViewModel.Factory(ServiceLocator.userRepository, ServiceLocator.authRepository)
        )[SettingsViewModel::class.java]

        binding.itemAvatar.setOnClickListener { pickImageLauncher.launch("image/*") }

        binding.itemName.setOnClickListener {
            val text = binding.editName.text.toString()
            viewModel.updateName(text)
        }

        binding.itemSignature.setOnClickListener {
            val text = binding.editSignature.text.toString()
            viewModel.updateSignature(text)
        }

        binding.itemLanguage.setOnClickListener {
            val text = binding.editLanguage.text.toString()
            viewModel.updateLanguage(text)
        }

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            requireActivity().finish() // 简单处理：退出到启动页
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
