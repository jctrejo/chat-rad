package com.chat.radar.data.model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.util.*

@Parcelize
data class Note(
    var id: String = "",
    var user_id: String = "",
    val title: String = "",
    val description: String = "",
    val tags: MutableList<String> = arrayListOf(),
    val images: List<String> = arrayListOf(),
    @ServerTimestamp
    val date: Date = Date(),
    val status: Int = 0,
    var order: @RawValue List<OrderArrayItem> = arrayListOf(),
    val phone: String? = "",
    val membership: Int? = 0,
    val its_new_order: Boolean? = false
) : Parcelable


