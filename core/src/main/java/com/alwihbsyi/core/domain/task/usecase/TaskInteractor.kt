package com.alwihbsyi.core.domain.task.usecase

import com.alwihbsyi.core.data.Resource
import com.alwihbsyi.core.domain.task.model.Task
import com.alwihbsyi.core.domain.task.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import java.io.File

class TaskInteractor(private val taskRepository: TaskRepository): TaskUseCase {
    override fun getTasks(): Flow<Resource<List<Task>>> =
        taskRepository.getTasks()

    override fun getTaskDetail(dueDate: String): Flow<Resource<List<Task>>> =
        taskRepository.getTaskDetail(dueDate)

    override fun setTask(task: Task, file: File): Flow<Resource<String>> =
        taskRepository.setTask(task, file)

    override fun updateTask(task: Task): Flow<Resource<String>> =
        taskRepository.updateTask(task)
}