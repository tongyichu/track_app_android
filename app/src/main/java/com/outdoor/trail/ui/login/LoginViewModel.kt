package com.outdoor.trail.ui.login

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.outdoor.trail.BuildConfig
import com.outdoor.trail.data.local.TokenManager
import com.outdoor.trail.data.model.LoginResponse
import com.outdoor.trail.data.repository.UserRepository
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import kotlinx.coroutines.launch

/**
 * 登录页ViewModel，处理微信登录和手机号登录逻辑
 */
class LoginViewModel : ViewModel() {

    private val repository = UserRepository()

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    /**
     * 发起微信登录
     * 调用微信SDK拉起微信授权页面，用户同意后回调WXEntryActivity
     */
    fun wechatLogin(activity: Activity) {
        val api = WXAPIFactory.createWXAPI(activity, BuildConfig.WECHAT_APP_ID, true)
        api.registerApp(BuildConfig.WECHAT_APP_ID)

        if (!api.isWXAppInstalled) {
            _loginResult.value = Result.failure(Exception("未安装微信"))
            return
        }

        val req = SendAuth.Req()
        req.scope = "snsapi_userinfo"
        req.state = "outdoor_trail_login"
        api.sendReq(req)
    }

    /**
     * 使用微信授权码完成登录（由WXEntryActivity回调）
     */
    fun completeWechatLogin(code: String) {
        _isLoading.value = true
        viewModelScope.launch {
            repository.wechatLogin(code).fold(
                onSuccess = { response ->
                    saveLoginInfo(response)
                    _loginResult.value = Result.success(response)
                    _isLoading.value = false
                },
                onFailure = { e ->
                    _loginResult.value = Result.failure(e)
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * 手机号+验证码登录
     */
    fun phoneLogin(phone: String, smsCode: String) {
        _isLoading.value = true
        viewModelScope.launch {
            repository.phoneLogin(phone, smsCode).fold(
                onSuccess = { response ->
                    saveLoginInfo(response)
                    _loginResult.value = Result.success(response)
                    _isLoading.value = false
                },
                onFailure = { e ->
                    _loginResult.value = Result.failure(e)
                    _isLoading.value = false
                }
            )
        }
    }

    /** 保存登录信息到本地安全存储 */
    private fun saveLoginInfo(response: LoginResponse) {
        TokenManager.saveLoginInfo(
            token = response.token,
            userId = response.userId,
            nickname = response.nickname,
            avatar = response.avatarUrl
        )
    }
}
