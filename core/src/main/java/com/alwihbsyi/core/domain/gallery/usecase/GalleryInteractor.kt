package com.alwihbsyi.core.domain.gallery.usecase

import com.alwihbsyi.core.data.Resource
import com.alwihbsyi.core.domain.gallery.model.Gallery
import com.alwihbsyi.core.domain.gallery.repository.GalleryRepository
import kotlinx.coroutines.flow.Flow
import java.io.File

class GalleryInteractor(private val galleryRepository: GalleryRepository): GalleryUseCase {
    override fun setGallery(gallery: Gallery, file: File): Flow<Resource<String>> =
        galleryRepository.setGallery(gallery, file)

    override fun getGalleries(): Flow<Resource<List<Gallery>>> =
        galleryRepository.getGalleries()
}