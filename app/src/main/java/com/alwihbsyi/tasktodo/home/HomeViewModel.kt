package com.alwihbsyi.tasktodo.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.alwihbsyi.core.domain.task.model.Task
import com.alwihbsyi.core.domain.task.usecase.TaskUseCase

class HomeViewModel(private val taskUseCase: TaskUseCase): ViewModel() {
    fun getTasks() = taskUseCase.getTasks().asLiveData()
    fun updateTask(task: Task) = taskUseCase.updateTask(task).asLiveData()
}