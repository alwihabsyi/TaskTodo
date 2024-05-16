package com.alwihbsyi.core.data

import com.alwihbsyi.core.data.source.FirebaseResponse
import com.alwihbsyi.core.data.source.RemoteDataSource
import com.alwihbsyi.core.domain.gallery.model.Gallery
import com.alwihbsyi.core.domain.gallery.repository.GalleryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.io.File

class GalleryRepositoryImpl(
    private val remoteDataSource: RemoteDataSource
): GalleryRepository {
    override fun setGallery(gallery: Gallery, file: File): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        when(val firebaseResponse = remoteDataSource.uploadGalleryFile(gallery, file).first()) {
            is FirebaseResponse.Error -> {
                emit(Resource.Error(firebaseResponse.errorMessage))
            }
            is FirebaseResponse.Success -> {
                emit(Resource.Success(firebaseResponse.data))
            }
            else -> {}
        }
    }

    override fun getGalleries(): Flow<Resource<List<Gallery>>> = flow {
        emit(Resource.Loading())
        when(val firebaseResponse = remoteDataSource.getGalleries().first()) {
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