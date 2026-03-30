package com.example.outdoortrack.core.prefs

import android.content.Context
import android.content.SharedPreferences
import java.util.Locale

/**
 * 对 SharedPreferences 的简单封装，负责管理用户 ID、登录时间、语言等信息。
 */
class AppPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("outdoor_track_prefs", Context.MODE_PRIVATE)

    var userId: String?
        get() = prefs.getString(KEY_USER_ID, null)
        set(value) {
            prefs.edit().putString(KEY_USER_ID, value).apply()
        }

    var lastLoginTimestamp: Long
        get() = prefs.getLong(KEY_LAST_LOGIN_TS, 0L)
        set(value) {
            prefs.edit().putLong(KEY_LAST_LOGIN_TS, value).apply()
        }

    var clientLanguage: String
        get() = prefs.getString(KEY_CLIENT_LANG, Locale.getDefault().toLanguageTag())
            ?: Locale.getDefault().toLanguageTag()
        set(value) {
            prefs.edit().putString(KEY_CLIENT_LANG, value).apply()
        }

    fun clearSession() {
        prefs.edit()
            .remove(KEY_USER_ID)
            .remove(KEY_LAST_LOGIN_TS)
            .apply()
    }

    companion object {
        private const val KEY_USER_ID = "key_user_id"
        private const val KEY_LAST_LOGIN_TS = "key_last_login_ts"
        private const val KEY_CLIENT_LANG = "key_client_lang"
    }
}
