package com.alwihbsyi.tasktodo

import android.app.Application
import com.alwihbsyi.core.di.firebaseModule
import com.alwihbsyi.core.di.repositoryModule
import com.alwihbsyi.tasktodo.di.useCaseModule
import com.alwihbsyi.tasktodo.di.viewModelModule
import com.google.firebase.FirebaseApp
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class TodoTaskApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@TodoTaskApplication)
            modules(
                listOf(
                    firebaseModule,
                    useCaseModule,
                    repositoryModule,
                    viewModelModule
                )
            )
        }
    }
}