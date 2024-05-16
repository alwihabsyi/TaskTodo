package com.alwihbsyi.tasktodo.di

import com.alwihbsyi.core.domain.auth.usecase.AuthInteractor
import com.alwihbsyi.core.domain.auth.usecase.AuthUseCase
import com.alwihbsyi.core.domain.gallery.usecase.GalleryInteractor
import com.alwihbsyi.core.domain.gallery.usecase.GalleryUseCase
import com.alwihbsyi.core.domain.task.usecase.TaskInteractor
import com.alwihbsyi.core.domain.task.usecase.TaskUseCase
import com.alwihbsyi.tasktodo.account.AccountViewModel
import com.alwihbsyi.tasktodo.auth.AuthViewModel
import com.alwihbsyi.tasktodo.gallery.GalleryViewModel
import com.alwihbsyi.tasktodo.home.HomeViewModel
import com.alwihbsyi.tasktodo.task.TaskViewModel
import org.koin.dsl.module

val useCaseModule = module {
    factory<AuthUseCase> { AuthInteractor(get()) }
    factory<TaskUseCase> { TaskInteractor(get()) }
    factory<GalleryUseCase> { GalleryInteractor(get()) }
}

val viewModelModule = module {
    single { AuthViewModel(get()) }
    single { TaskViewModel(get()) }
    single { HomeViewModel(get()) }
    single { GalleryViewModel(get()) }
    single { AccountViewModel(get()) }
}