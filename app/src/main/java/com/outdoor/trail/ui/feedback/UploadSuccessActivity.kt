package com.outdoor.trail.ui.feedback

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.outdoor.trail.databinding.ActivityUploadSuccessBinding
import com.outdoor.trail.ui.home.HomeActivity

/**
 * 上传成功反馈页Activity（静态页面）
 * 展示上传成功的提示信息，点击"返回首页"跳转到首页
 */
class UploadSuccessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadSuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }
}
