package com.chat.radar.domain.local.preferences

interface Preferences {

    fun put(key: String, value: String)

    fun put(key: String, value: Int)

    fun put(key: String, value: Long)

    fun put(key: String, value: Boolean)

    fun getString(key: String, value: String = ""): String

    fun getInt(key: String, value: Int = 0): Int

    fun getLong(key: String, value: Long = 0L): Long

    fun getBoolean(key: String, value: Boolean = false): Boolean
}
