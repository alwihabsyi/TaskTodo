package com.alwihbsyi.core.domain.auth.usecase

import com.alwihbsyi.core.data.Resource
import com.alwihbsyi.core.domain.auth.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class AuthInteractor(private val authRepository: AuthRepository): AuthUseCase {
    override fun signIn(email: String, password: String): Flow<Resource<String>> =
        authRepository.signIn(email, password)

    override fun signUp(email: String, password: String): Flow<Resource<String>> =
        authRepository.signUp(email, password)

    override fun checkUser(): Flow<Boolean> =
        authRepository.checkUser()
}