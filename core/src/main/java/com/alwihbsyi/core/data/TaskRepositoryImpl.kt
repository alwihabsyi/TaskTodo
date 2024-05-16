package com.alwihbsyi.core.data

import com.alwihbsyi.core.data.source.FirebaseResponse
import com.alwihbsyi.core.data.source.RemoteDataSource
import com.alwihbsyi.core.domain.task.model.Task
import com.alwihbsyi.core.domain.task.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.io.File

class TaskRepositoryImpl(
    private val remoteDataSource: RemoteDataSource
): TaskRepository {
    override fun getTasks(): Flow<Resource<List<Task>>> = flow {
        emit(Resource.Loading())
        when(val firebaseResponse = remoteDataSource.getAllTasks().first()) {
            is FirebaseResponse.Success -> {
                emit(Resource.Success(firebaseResponse.data))
            }
            FirebaseResponse.Empty -> {
                emit(Resource.Error("No tasks available"))
            }
            is FirebaseResponse.Error -> {
                emit(Resource.Error(firebaseResponse.errorMessage))
            }
        }
    }

    override fun getTaskDetail(dueDate: String): Flow<Resource<List<Task>>> = flow {
        emit(Resource.Loading())
        when(val firebaseResponse = remoteDataSource.getTaskDetail(dueDate).first()) {
            FirebaseResponse.Empty -> {
                emit(Resource.Error("No tasks due to this date"))
            }
            is FirebaseResponse.Error -> {
                emit(Resource.Error(firebaseResponse.errorMessage))
            }
            is FirebaseResponse.Success -> {
                emit(Resource.Success(firebaseResponse.data))
            }
        }
    }

    override fun setTask(task: Task, file: File): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        when(val firebaseResponse = remoteDataSource.uploadFile(task, file).first()) {
            is FirebaseResponse.Error -> {
                emit(Resource.Error(firebaseResponse.errorMessage))
            }
            is FirebaseResponse.Success -> {
                emit(Resource.Success(firebaseResponse.data))
            }
            else -> {}
        }
    }

    override fun updateTask(task: Task): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        when(val firebaseResponse = remoteDataSource.updateTask(task).first()) {
            is FirebaseResponse.Error -> {
                emit(Resource.Error(firebaseResponse.errorMessage))
            }
            is FirebaseResponse.Success -> {
                emit(Resource.Success(firebaseResponse.data))
            }
            else -> {}
        }
    }
}