package com.outdoor.trail.ui.settings

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.outdoor.trail.data.local.TokenManager
import com.outdoor.trail.databinding.ActivitySettingsBinding
import com.outdoor.trail.ui.login.LoginActivity

/**
 * 设置页Activity
 * 支持修改头像、用户名、签名、系统语言，以及退出登录
 */
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by viewModels()

    // 图片选择器
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            // TODO: 上传图片到服务器获取URL，然后调用updatePhoto
            viewModel.updatePhoto(TokenManager.getUserId(), uri.toString())
        }
    }

    // 相机拍照
    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            // TODO: 上传bitmap到服务器获取URL
            Toast.makeText(this, "头像已更新", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.ivBack.setOnClickListener { finish() }

        // 修改头像
        binding.layoutAvatar.setOnClickListener {
            val options = arrayOf("拍照", "从相册选择")
            AlertDialog.Builder(this)
                .setTitle("修改头像")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> takePicture.launch(null)
                        1 -> pickImage.launch("image/*")
                    }
                }.show()
        }

        // 修改用户名
        binding.layoutNickname.setOnClickListener {
            showEditDialog("修改用户名", TokenManager.getNickname()) { newName ->
                viewModel.updateName(TokenManager.getUserId(), newName)
            }
        }

        // 修改签名
        binding.layoutSignature.setOnClickListener {
            showEditDialog("修改签名", "") { newSig ->
                viewModel.updateSignature(TokenManager.getUserId(), newSig)
            }
        }

        // 修改语言
        binding.layoutLanguage.setOnClickListener {
            val languages = arrayOf("简体中文", "English", "日本語")
            val langCodes = arrayOf("zh-CN", "en-US", "ja-JP")
            AlertDialog.Builder(this)
                .setTitle("选择语言")
                .setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, languages)) { _, which ->
                    val code = langCodes[which]
                    viewModel.updateLanguage(TokenManager.getUserId(), code)
                    TokenManager.setClientLanguage(code)
                }.show()
        }

        // 退出登录
        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("退出登录")
                .setMessage("确定要退出当前账号吗？")
                .setPositiveButton("确定") { _, _ ->
                    TokenManager.logout()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("取消", null)
                .show()
        }
    }

    private fun observeViewModel() {
        viewModel.updateSuccess.observe(this) { msg ->
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
        viewModel.error.observe(this) { msg ->
            msg?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        }
    }

    /** 显示文本编辑弹窗 */
    private fun showEditDialog(title: String, currentValue: String, onConfirm: (String) -> Unit) {
        val editText = EditText(this).apply { setText(currentValue) }
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(editText)
            .setPositiveButton("确定") { _, _ ->
                val value = editText.text.toString().trim()
                if (value.isNotEmpty()) onConfirm(value)
            }
            .setNegativeButton("取消", null)
            .show()
    }
}
