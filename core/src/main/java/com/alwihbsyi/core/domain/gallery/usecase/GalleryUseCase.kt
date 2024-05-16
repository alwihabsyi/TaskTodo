package com.alwihbsyi.core.domain.gallery.usecase

import com.alwihbsyi.core.data.Resource
import com.alwihbsyi.core.domain.gallery.model.Gallery
import kotlinx.coroutines.flow.Flow
import java.io.File

interface GalleryUseCase {
    fun setGallery(gallery: Gallery, file: File): Flow<Resource<String>>
    fun getGalleries(): Flow<Resource<List<Gallery>>>
}