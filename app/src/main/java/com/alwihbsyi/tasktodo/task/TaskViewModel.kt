package com.alwihbsyi.tasktodo.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.alwihbsyi.core.domain.task.model.Task
import com.alwihbsyi.core.domain.task.usecase.TaskUseCase
import java.io.File

class TaskViewModel(private val taskUseCase: TaskUseCase): ViewModel() {
    fun createTask(task: Task, file: File) = taskUseCase.setTask(task, file).asLiveData()
    fun getTaskDetail(dueDate: String) = taskUseCase.getTaskDetail(dueDate).asLiveData()
}