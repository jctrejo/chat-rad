package com.chat.radar.data.repository.imp

import android.net.Uri
import com.chat.radar.common.Constants.EMPTY
import com.chat.radar.common.UiState
import com.chat.radar.data.model.Ingredients
import com.chat.radar.data.model.Note
import com.chat.radar.data.model.SizeProduct
import com.chat.radar.data.model.User
import com.chat.radar.data.repository.NoteRepository
import com.chat.radar.util.FireStoreCollection
import com.chat.radar.util.FireStoreDocumentField
import com.chat.radar.util.FirebaseStorageConstants.NOTE_IMAGES
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class NoteRepositoryImp(
    val database: FirebaseFirestore,
    val storageReference: StorageReference
) : NoteRepository {

    override fun getNotes(user: User?, result: (UiState<List<Note>>) -> Unit) {
        database.collection(FireStoreCollection.NOTE)
            .whereEqualTo(FireStoreDocumentField.USER_ID, user?.id)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                if (value != null) {
                    val notes = arrayListOf<Note>()
                    value.forEach { document ->
                        val note = document.toObject(Note::class.java)
                        notes.add(note)
                    }
                    result.invoke(
                        UiState.Success(notes.sortedByDescending { it.date })
                    )
                } else {
                    result.invoke(
                        UiState.Failure("")
                    )
                }
            }
    }

    override fun getMembership(membership: Int?, result: (UiState<List<Note>>) -> Unit) {
        database.collection(FireStoreCollection.NOTE)
            .whereEqualTo(FireStoreDocumentField.MEMBERSHIP, membership)
            .addSnapshotListener { value, _ ->

                if (value != null) {
                    val notes = arrayListOf<Note>()
                    value.forEach { document ->
                        val note = document.toObject(Note::class.java)
                        notes.add(note)
                    }
                    result.invoke(
                        UiState.Success(notes.sortedByDescending { it.date })
                    )
                } else {
                    result.invoke(
                        UiState.Failure(EMPTY)
                    )
                }
            }
    }


    override fun addNote(note: Note, result: (UiState<Pair<Note, String>>) -> Unit) {
        val document = database.collection(FireStoreCollection.NOTE).document()
        note.id = document.id
        document
            .set(note)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success(Pair(note, "Note has been created successfully"))
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override fun updateNote(note: Note, result: (UiState<String>) -> Unit) {
        val document = database.collection(FireStoreCollection.NOTE).document(note.id)
        document
            .set(note)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("Note has been update successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override fun deleteNote(note: Note, result: (UiState<String>) -> Unit) {
        database.collection(FireStoreCollection.NOTE).document(note.id)
            .delete()
            .addOnSuccessListener {
                result.invoke(UiState.Success("Note successfully deleted!"))
            }
            .addOnFailureListener { e ->
                result.invoke(UiState.Failure(e.message))
            }
    }

    override suspend fun uploadSingleFile(fileUri: Uri, onResult: (UiState<Uri>) -> Unit) {
        try {
            val uri: Uri = withContext(Dispatchers.IO) {
                storageReference
                    .putFile(fileUri)
                    .await()
                    .storage
                    .downloadUrl
                    .await()
            }
            onResult.invoke(UiState.Success(uri))
        } catch (e: FirebaseException) {
            onResult.invoke(UiState.Failure(e.message))
        } catch (e: Exception) {
            onResult.invoke(UiState.Failure(e.message))
        }
    }

    override suspend fun uploadMultipleFile(
        fileUri: List<Uri>,
        onResult: (UiState<List<Uri>>) -> Unit
    ) {
        try {
            val uri: List<Uri> = withContext(Dispatchers.IO) {
                fileUri.map { image ->
                    async {
                        storageReference.child(NOTE_IMAGES)
                            .child(image.lastPathSegment ?: "${System.currentTimeMillis()}")
                            .putFile(image)
                            .await()
                            .storage
                            .downloadUrl
                            .await()
                    }
                }.awaitAll()
            }
            onResult.invoke(UiState.Success(uri))
        } catch (e: FirebaseException) {
            onResult.invoke(UiState.Failure(e.message))
        } catch (e: Exception) {
            onResult.invoke(UiState.Failure(e.message))
        }
    }


    override fun getSizeProduct(user: User?, keySize: String, result: (UiState<SizeProduct>) -> Unit) {
        database.collection(FireStoreCollection.SIZE_PRODUCTS)
            .whereEqualTo(FireStoreDocumentField.USER_ID, keySize)
            .addSnapshotListener { value, e ->
                if (value != null) {
                    var sizes = SizeProduct()
                    value.forEach { document ->
                        val size = document.toObject(SizeProduct::class.java)
                        sizes = size
                    }
                    result.invoke(UiState.Success(sizes))
                } else {
                    result.invoke(
                        UiState.Failure("")
                    )
                }
            }
    }

    override fun getIngredients(user: User?, keyIngredients: String, result: (UiState<Ingredients>) -> Unit) {
        database.collection(FireStoreCollection.INGREDIENTS)
            .whereEqualTo(FireStoreDocumentField.USER_ID, keyIngredients)
            .addSnapshotListener { value, e ->
                if (value != null) {
                    var ingredients = Ingredients()
                    value.forEach { document ->
                        val ingredient = document.toObject(Ingredients::class.java)
                        ingredients = ingredient
                    }
                    result.invoke(
                        UiState.Success(ingredients)
                    )
                } else {
                    result.invoke(
                        UiState.Failure("")
                    )
                }
            }
    }
}
