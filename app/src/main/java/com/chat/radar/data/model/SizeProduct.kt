package com.chat.radar.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SizeProduct(
    var name: String? = "",
    var user_id: String? = "",
    var sizeList: List<String>? = emptyList(),
    ) : Parcelable
