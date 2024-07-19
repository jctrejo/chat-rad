package com.chat.radar.data.model

import com.google.gson.annotations.SerializedName


data class Response(
    val id: String,
    val video: Boolean = false,
    @SerializedName("object")
    var objectCustomer: String? = null,
    val address: String? = null,
    val balance: Int,
    val created: String,
    val currency: String? = null,
    val default_source: String? = null,
    val delinquent: Boolean,
    val description: String? = null,
    val discount: String? = null,
    val email: String? = null,
    val secret: String? = null,
    val invoice_prefix: String? = null,
    val livemode: Boolean? = null,
    val name: String? = null,
    val next_invoice_sequence: Int? = null,
)
