package com.chat.radar.di

import android.content.Context
import com.chat.radar.domain.local.preferences.Preferences
import com.chat.radar.domain.local.preferences.PreferencesImpl

object DataProvider {
    fun providesPreferences(context: Context): Preferences = PreferencesImpl.getInstance(context)
}
