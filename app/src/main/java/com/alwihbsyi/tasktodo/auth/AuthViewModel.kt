package com.alwihbsyi.tasktodo.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.alwihbsyi.core.domain.auth.usecase.AuthUseCase

class AuthViewModel(private val authUseCase: AuthUseCase): ViewModel() {
    fun signIn(email: String, password: String) =
        authUseCase.signIn(email, password).asLiveData()

    fun signUp(email: String, password: String) =
        authUseCase.signUp(email, password).asLiveData()

    fun checkUser() = authUseCase.checkUser().asLiveData()
}