package com.chat.radar.data.model

data class OrderArrayItem(
    val order: String? = "",
    val size: String? = "",
    val type: String? = "",
    val amount: String? = ""
)

data class SizeArrayItem(
    val sizeList: List<String> = emptyList(),
    val user_id: String? = null
)

data class IngredientsArrayItem(
    val ingredients: List<String> = emptyList(),
    val user_id: String? = null
)
