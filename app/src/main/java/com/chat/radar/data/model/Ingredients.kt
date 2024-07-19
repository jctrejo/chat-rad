package com.chat.radar.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Ingredients(
    var name: String? = "",
    var user_id: String? = "",
    var ingredients: List<String>? = emptyList(),
) : Parcelable