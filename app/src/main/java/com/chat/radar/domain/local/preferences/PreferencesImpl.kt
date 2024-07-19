package com.chat.radar.domain.local.preferences

import android.content.Context
import android.content.SharedPreferences

class PreferencesImpl(private val prefs: SharedPreferences) : Preferences {

    override fun put(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    override fun put(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    override fun put(key: String, value: Long) {
        prefs.edit().putLong(key, value).apply()
    }

    override fun put(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    override fun getString(key: String, value: String) = prefs.getString(key, value) ?: ""

    override fun getInt(key: String, value: Int) = prefs.getInt(key, value)

    override fun getLong(key: String, value: Long) = prefs.getLong(key, value)

    override fun getBoolean(key: String, value: Boolean) = prefs.getBoolean(key, value)

    companion object {
        private const val PREFS_NAME = "module_prefs"
        private var instance: Preferences? = null

        fun getInstance(context: Context): Preferences {
            if (instance == null) {
                instance =
                    PreferencesImpl(context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE))
            }
            return instance!!
        }
    }

}