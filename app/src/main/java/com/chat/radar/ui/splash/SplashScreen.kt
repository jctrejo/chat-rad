package com.chat.radar.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import com.android.chat_redar.databinding.ActivitySplashScreenBinding
import com.google.firebase.FirebaseApp
import com.chat.radar.App
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashScreen : AppCompatActivity() {
    private val splashTimeout: Long = 5000
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)
        setupTimeSplash()
    }

    private fun setupTimeSplash() {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, App::class.java))
            finish()
        }, splashTimeout)
    }
}
