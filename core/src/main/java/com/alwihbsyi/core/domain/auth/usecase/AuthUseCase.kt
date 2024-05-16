package com.alwihbsyi.core.domain.auth.usecase

import com.alwihbsyi.core.data.Resource
import kotlinx.coroutines.flow.Flow

interface AuthUseCase {
    fun signIn(email: String, password: String): Flow<Resource<String>>
    fun signUp(email: String, password: String): Flow<Resource<String>>
    fun checkUser(): Flow<Boolean>
}