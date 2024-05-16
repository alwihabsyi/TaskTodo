package com.alwihbsyi.core.domain.task.usecase

import com.alwihbsyi.core.data.Resource
import com.alwihbsyi.core.domain.task.model.Task
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.util.Date

interface TaskUseCase {
    fun getTasks(): Flow<Resource<List<Task>>>
    fun getTaskDetail(dueDate: String): Flow<Resource<List<Task>>>
    fun setTask(task: Task, file: File): Flow<Resource<String>>
    fun updateTask(task: Task): Flow<Resource<String>>
}