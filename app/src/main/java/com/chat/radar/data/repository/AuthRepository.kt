package com.chat.radar.data.repository

import com.chat.radar.data.model.IngredientsArrayItem
import com.chat.radar.data.model.SizeArrayItem
import com.chat.radar.data.model.User
import com.chat.radar.common.UiState

interface AuthRepository {
    fun registerUser(email: String, password: String, user: User, result: (UiState<String>) -> Unit)
    fun updateUserInfo(user: User, result: (UiState<String>) -> Unit)
    fun loginUser(email: String, password: String, result: (UiState<String>) -> Unit)
    fun forgotPassword(email: String, result: (UiState<String>) -> Unit)
    fun logout(result: () -> Unit)
    fun storeSession(id: String, result: (User?) -> Unit)
    fun getSession(result: (User?) -> Unit)
    fun registerIngredients(ingredients: IngredientsArrayItem, user: User, result: (UiState<String>) -> Unit)
    fun registerSize(size: SizeArrayItem, user: User, keySize: String, result: (UiState<String>) -> Unit)
}
