package com.chat.radar.util

import android.content.Context
import android.content.SharedPreferences

class DataProccessor(var context: Context) {

    companion object {
        const val PREFS_NAME = "appname_prefs"
    }

    fun sharedPreferenceExist(key: String?): Boolean {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, 0)
        return if (!prefs.contains(key)) {
            true
        } else {
            false
        }
    }

    fun setInt(key: String?, value: Int) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, 0)
        val editor = prefs.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun removeValue(key: String?) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, 0)
        val editor = prefs.edit()
        editor.remove(key)
        editor.apply()
    }

    fun getInt(key: String?): Int {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, 0)
        return prefs.getInt(key, -1)
    }

    fun setStr(key: String?, value: String?) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, 0)
        val editor = prefs.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getStr(key: String?): String? {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, 0)
        return prefs.getString(key, "DNF")
    }

    fun setBool(key: String?, value: Boolean) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, 0)
        val editor = prefs.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBool(key: String?): Boolean {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, 0)
        return prefs.getBoolean(key, false)
    }
}