package com.example.outdoortrack.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.outdoortrack.R
import com.example.outdoortrack.core.ServiceLocator
import com.example.outdoortrack.databinding.FragmentProfileBinding

/**
 * 个人中心页面：展示用户信息，入口进入设置页。
 */
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            ProfileViewModel.Factory(ServiceLocator.userRepository)
        )[ProfileViewModel::class.java]

        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
        }

        viewModel.user.observe(viewLifecycleOwner) { user ->
            binding.tvName.text = user.nickname ?: "未命名"
            binding.tvSignature.text = user.signature ?: "这个人很神秘，什么都没写"
            Glide.with(binding.ivAvatar)
                .load(user.avatarUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(binding.ivAvatar)
        }

        viewModel.load()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
