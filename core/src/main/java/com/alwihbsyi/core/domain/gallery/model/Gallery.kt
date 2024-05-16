package com.alwihbsyi.core.domain.gallery.model

import java.util.UUID

data class Gallery(
    val id: String = UUID.randomUUID().toString(),
    val name: String? = null,
    val file: String? = null
)
