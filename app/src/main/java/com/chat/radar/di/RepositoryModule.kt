package com.chat.radar.di

import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.chat.radar.data.repository.AuthRepository
import com.chat.radar.data.repository.NoteRepository
import com.chat.radar.data.repository.TaskRepository
import com.chat.radar.data.repository.imp.AuthRepositoryImp
import com.chat.radar.data.repository.imp.NoteRepositoryImp
import com.chat.radar.data.repository.imp.TaskRepositoryImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Provides
    @Singleton
    fun provideNoteRepository(
        database: FirebaseFirestore,
        storageReference: StorageReference
    ): NoteRepository{
        return NoteRepositoryImp(database,storageReference)
    }

    @Provides
    @Singleton
    fun provideTaskRepository(
        database: FirebaseDatabase
    ): TaskRepository{
        return TaskRepositoryImp(database)
    }

    @Provides
    @Singleton
    fun provideAutghRepository(
        database: FirebaseFirestore,
        auth: FirebaseAuth,
        appPreferences: SharedPreferences,
        gson: Gson
    ): AuthRepository {
        return AuthRepositoryImp(auth,database,appPreferences,gson)
    }
}
