package com.alwihbsyi.core.domain.auth.repository

import com.alwihbsyi.core.data.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun signIn(email: String, password: String): Flow<Resource<String>>
    fun signUp(email: String, password: String): Flow<Resource<String>>
    fun checkUser(): Flow<Boolean>
}