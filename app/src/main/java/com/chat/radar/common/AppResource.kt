package com.chat.radar.common

sealed class AppResource<out T> {
    object Loading : AppResource<Nothing>()
    data class Success<T>(val item: T) : AppResource<T>()
    data class Error(val throwable: String) : AppResource<Nothing>()
}
