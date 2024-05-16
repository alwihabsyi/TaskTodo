package com.alwihbsyi.core.di

import com.alwihbsyi.core.data.AuthRepositoryImpl
import com.alwihbsyi.core.data.GalleryRepositoryImpl
import com.alwihbsyi.core.data.TaskRepositoryImpl
import com.alwihbsyi.core.data.source.RemoteDataSource
import com.alwihbsyi.core.domain.auth.repository.AuthRepository
import com.alwihbsyi.core.domain.gallery.repository.GalleryRepository
import com.alwihbsyi.core.domain.task.repository.TaskRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import org.koin.dsl.module

val firebaseModule = module {
    single { Firebase.firestore }
    single { FirebaseAuth.getInstance() }
    single { Firebase.storage }
}

val repositoryModule = module {
    single { RemoteDataSource(get(), get(), get()) }
    single<AuthRepository> {
        AuthRepositoryImpl(get())
    }
    single<TaskRepository> {
        TaskRepositoryImpl(get())
    }
    single<GalleryRepository> {
        GalleryRepositoryImpl(get())
    }
}