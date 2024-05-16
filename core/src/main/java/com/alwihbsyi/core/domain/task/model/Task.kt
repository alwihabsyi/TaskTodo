package com.alwihbsyi.core.domain.task.model

import com.alwihbsyi.core.utils.Constants.TO_DO
import java.util.Random
import java.util.UUID

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val notificationId: Int = Random().nextInt(1000),
    val title: String? = null,
    val description: String? = null,
    val dueDate: String? = null,
    val status: String = TO_DO,
    val file: String? = null
)