package com.chat.radar.data.repository

import android.net.Uri
import com.chat.radar.data.model.Ingredients
import com.chat.radar.data.model.Note
import com.chat.radar.data.model.SizeProduct
import com.chat.radar.data.model.User
import com.chat.radar.common.UiState

interface NoteRepository {
    fun getNotes(user: User?, result: (UiState<List<Note>>) -> Unit)
    fun addNote(note: Note, result: (UiState<Pair<Note, String>>) -> Unit)
    fun updateNote(note: Note, result: (UiState<String>) -> Unit)
    fun deleteNote(note: Note, result: (UiState<String>) -> Unit)
    suspend fun uploadSingleFile(fileUri: Uri, onResult: (UiState<Uri>) -> Unit)
    suspend fun uploadMultipleFile(fileUri: List<Uri>, onResult: (UiState<List<Uri>>) -> Unit)
    fun getMembership(membership: Int?, result: (UiState<List<Note>>) -> Unit)
    fun getSizeProduct(user: User?, idSize: String, result: (UiState<SizeProduct>) -> Unit)
    fun getIngredients(user: User?, idIngredients: String, result: (UiState<Ingredients>) -> Unit)

}