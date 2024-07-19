package com.chat.radar.ui.imageview

import android.annotation.SuppressLint
import android.os.Bundle
import android.transition.Fade
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.chat_redar.R
import com.jsibbold.zoomage.ZoomageView
import com.squareup.picasso.Picasso

class ViewImageActivity : AppCompatActivity() {
    var imageUrl: String? = null
    var imageView: ZoomageView? = null

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image)
        title = "Image"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        imageUrl = intent.getStringExtra("imageUrl")
        imageView = findViewById(R.id.imageView)
        Picasso.get().load(imageUrl).placeholder(R.drawable.ic_gallery).into(imageView)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() === android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}