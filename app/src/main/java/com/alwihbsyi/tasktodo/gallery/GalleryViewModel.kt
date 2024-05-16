package com.alwihbsyi.tasktodo.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.alwihbsyi.core.domain.gallery.model.Gallery
import com.alwihbsyi.core.domain.gallery.usecase.GalleryUseCase
import java.io.File

class GalleryViewModel(private val galleryUseCase: GalleryUseCase): ViewModel() {
    fun setGallery(gallery: Gallery, file: File) = galleryUseCase.setGallery(gallery, file).asLiveData()
    fun getGalleries() = galleryUseCase.getGalleries().asLiveData()
}