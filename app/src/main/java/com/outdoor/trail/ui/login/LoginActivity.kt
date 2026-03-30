package com.outdoor.trail.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.outdoor.trail.data.local.TokenManager
import com.outdoor.trail.databinding.ActivityLoginBinding
import com.outdoor.trail.ui.home.HomeActivity

/**
 * 登录页Activity
 * 支持微信登录和手机号登录两种方式
 * 用户未登录时的唯一可见页面
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 检查是否已登录（15天内有活跃）
        if (TokenManager.isLoggedIn()) {
            TokenManager.updateLastActive()
            navigateToHome()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.btnWechatLogin.setOnClickListener {
            viewModel.wechatLogin(this)
        }

        binding.btnPhoneLogin.setOnClickListener {
            val phone = binding.etPhone.text.toString().trim()
            val code = binding.etSmsCode.text.toString().trim()
            if (phone.isEmpty() || code.isEmpty()) {
                Toast.makeText(this, "请输入手机号和验证码", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.phoneLogin(phone, code)
        }

        binding.btnSendCode.setOnClickListener {
            val phone = binding.etPhone.text.toString().trim()
            if (phone.length != 11) {
                Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Toast.makeText(this, "验证码已发送", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeViewModel() {
        viewModel.loginResult.observe(this) { result ->
            result.fold(
                onSuccess = { navigateToHome() },
                onFailure = { Toast.makeText(this, "登录失败: ${it.message}", Toast.LENGTH_SHORT).show() }
            )
        }
        viewModel.isLoading.observe(this) { loading ->
            binding.btnWechatLogin.isEnabled = !loading
            binding.btnPhoneLogin.isEnabled = !loading
        }
    }

    private fun navigateToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}
