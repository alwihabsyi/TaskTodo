package com.alwihbsyi.core.data

import com.alwihbsyi.core.data.source.FirebaseResponse
import com.alwihbsyi.core.data.source.RemoteDataSource
import com.alwihbsyi.core.domain.auth.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class AuthRepositoryImpl(
    private val remoteDataSource: RemoteDataSource
): AuthRepository {
    override fun signIn(email: String, password: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        when(val firebaseResponse = remoteDataSource.signIn(email, password).first()) {
            is FirebaseResponse.Error -> {
                emit(Resource.Error(firebaseResponse.errorMessage))
            }
            is FirebaseResponse.Success -> {
                emit(Resource.Success(firebaseResponse.data))
            }
            else -> {}
        }
    }

    override fun signUp(email: String, password: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        when(val firebaseResponse = remoteDataSource.signUp(email, password).first()) {
            is FirebaseResponse.Error -> {
                emit(Resource.Error(firebaseResponse.errorMessage))
            }
            is FirebaseResponse.Success -> {
                emit(Resource.Success(firebaseResponse.data))
            }
            else -> {}
        }
    }

    override fun checkUser(): Flow<Boolean> = flow {
        emit(remoteDataSource.checkUser().first())
    }
}