package com.outdoor.trail.wxapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.outdoor.trail.BuildConfig
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory

/**
 * 微信SDK回调Activity
 * 处理微信登录授权结果
 */
class WXEntryActivity : Activity(), IWXAPIEventHandler {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val api = WXAPIFactory.createWXAPI(this, BuildConfig.WECHAT_APP_ID, false)
        api.handleIntent(intent, this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val api = WXAPIFactory.createWXAPI(this, BuildConfig.WECHAT_APP_ID, false)
        api.handleIntent(intent, this)
    }

    override fun onReq(req: BaseReq?) {}

    override fun onResp(resp: BaseResp?) {
        if (resp?.type == ConstantsAPI.COMMAND_SENDAUTH) {
            val authResp = resp as SendAuth.Resp
            when (authResp.errCode) {
                BaseResp.ErrCode.ERR_OK -> {
                    val code = authResp.code
                    // 通知LoginViewModel完成登录
                }
                BaseResp.ErrCode.ERR_USER_CANCEL -> {}
                else -> {}
            }
        }
        finish()
    }
}
