package com.example.outdoortrack.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.outdoortrack.R
import com.example.outdoortrack.core.ServiceLocator
import com.example.outdoortrack.databinding.FragmentLoginBinding

/**
 * 登录页：支持手机号登录（本地 Session）和微信登录占位按钮。
 */
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            LoginViewModel.Factory(ServiceLocator.authRepository)
        )[LoginViewModel::class.java]

        binding.btnPhoneLogin.setOnClickListener {
            val phone = binding.editPhone.text.toString()
            try {
                viewModel.loginWithPhone(phone)
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            } catch (e: IllegalArgumentException) {
                Toast.makeText(requireContext(), "手机号格式不正确", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnWechatLogin.setOnClickListener {
            Toast.makeText(requireContext(), "微信登录为占位，需接入微信 SDK 后实现", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
