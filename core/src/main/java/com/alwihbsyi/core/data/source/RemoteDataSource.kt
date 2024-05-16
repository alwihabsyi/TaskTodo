package com.alwihbsyi.core.data.source

import com.alwihbsyi.core.domain.gallery.model.Gallery
import com.alwihbsyi.core.domain.task.model.Task
import com.alwihbsyi.core.utils.Constants.GALLERY_COLLECTION
import com.alwihbsyi.core.utils.Constants.TASK_COLLECTION
import com.alwihbsyi.core.utils.Constants.USER_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RemoteDataSource(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
) {
    fun getAllTasks(): Flow<FirebaseResponse<List<Task>>> =
        callbackFlow {
            val listenerRegistration = firestore.collection(USER_COLLECTION).document(auth.uid!!)
                .collection(TASK_COLLECTION)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        trySend(FirebaseResponse.Error(error.toString()))
                        return@addSnapshotListener
                    }

                    value?.toObjects(Task::class.java)?.let {
                        if (it.isEmpty()) {
                            trySend(FirebaseResponse.Empty)
                        } else {
                            trySend(FirebaseResponse.Success(it))
                        }
                    }
                }

            awaitClose { listenerRegistration.remove() }
        }

    fun getTaskDetail(dueDate: String): Flow<FirebaseResponse<List<Task>>> =
        callbackFlow {
            val listenerRegistration = firestore.collection(USER_COLLECTION).document(auth.uid!!)
                .collection(TASK_COLLECTION)
                .whereEqualTo("dueDate", dueDate)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        trySend(FirebaseResponse.Error("Terjadi kesalahan saat mengambil data"))
                        return@addSnapshotListener
                    }

                    value?.toObjects(Task::class.java)?.let {
                        if (it.isEmpty()) {
                            trySend(FirebaseResponse.Empty)
                        } else {
                            trySend(FirebaseResponse.Success(it))
                        }
                    }
                }

            awaitClose { listenerRegistration.remove() }
        }

    suspend fun uploadFile(task: Task, file: File): Flow<FirebaseResponse<String>> =
        flow {
            try {
                val docByteArray = file.readBytes()
                val imageStorage = storage.reference.child("user/${auth.uid}/task/${task.id}")
                val result = imageStorage.putBytes(docByteArray).await()
                val downloadUrl = result.storage.downloadUrl.await().toString()
                val response = setTask(task.copy(file = downloadUrl))
                emit(response)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    private suspend fun setTask(task: Task): FirebaseResponse<String> =
        suspendCoroutine { continuation ->
            firestore.collection(USER_COLLECTION).document(auth.uid!!)
                .collection(TASK_COLLECTION).document(task.id).set(task)
                .addOnFailureListener {
                    it.printStackTrace()
                    continuation.resume(FirebaseResponse.Error("Terjadi kesalahan pada server"))
                }
                .addOnSuccessListener {
                    continuation.resume(FirebaseResponse.Success("Berhasil menambah task"))
                }
        }

    fun signIn(email: String, password: String): Flow<FirebaseResponse<String>> =
        callbackFlow {
            auth.signInWithEmailAndPassword(email, password)
                .addOnFailureListener {
                    trySend(FirebaseResponse.Error("Terjadi kesalahan"))
                }
                .addOnSuccessListener {
                    trySend(FirebaseResponse.Success("Berhasil masuk"))
                }

            awaitClose()
        }

    fun signUp(email: String, password: String): Flow<FirebaseResponse<String>> =
        callbackFlow {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnFailureListener {
                    trySend(FirebaseResponse.Error("Terjadi kesalahan"))
                }
                .addOnSuccessListener {
                    trySend(FirebaseResponse.Success("Berhasil masuk"))
                }

            awaitClose()
        }

    fun checkUser(): Flow<Boolean> = flow {
        emit(auth.currentUser != null)
    }

    fun updateTask(task: Task): Flow<FirebaseResponse<String>> =
        callbackFlow {
            firestore.collection(USER_COLLECTION).document(auth.uid!!).collection(TASK_COLLECTION)
                .document(task.id).set(task)
                .addOnFailureListener {
                    trySend(FirebaseResponse.Error("Terjadi kesalahan"))
                }
                .addOnSuccessListener {
                    trySend(FirebaseResponse.Success("Status berhasil diubah"))
                }

            awaitClose()
        }

    fun getGalleries(): Flow<FirebaseResponse<List<Gallery>>> =
        callbackFlow {
            val listenerRegistration = firestore.collection(USER_COLLECTION).document(auth.uid!!).collection(GALLERY_COLLECTION)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        trySend(FirebaseResponse.Error(error.toString()))
                        return@addSnapshotListener
                    }

                    value?.toObjects(Gallery::class.java)?.let {
                        if (it.isEmpty()) {
                            trySend(FirebaseResponse.Empty)
                        } else {
                            trySend(FirebaseResponse.Success(it))
                        }
                    }
                }

            awaitClose { listenerRegistration.remove() }
        }

    fun uploadGalleryFile(gallery: Gallery, file: File): Flow<FirebaseResponse<String>> =
        flow {
            try {
                val docByteArray = file.readBytes()
                val imageStorage = storage.reference.child("user/${auth.uid}/gallery/${gallery.id}")
                val result = imageStorage.putBytes(docByteArray).await()
                val downloadUrl = result.storage.downloadUrl.await().toString()
                val response = setGallery(gallery.copy(file = downloadUrl))
                emit(response)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    private suspend fun setGallery(gallery: Gallery): FirebaseResponse<String> =
        suspendCoroutine { cn ->
            firestore.collection(USER_COLLECTION).document(auth.uid!!).collection(GALLERY_COLLECTION)
                .document(gallery.id).set(gallery)
                .addOnFailureListener {
                    cn.resume(FirebaseResponse.Error("Terjadi kesalahan"))
                }
                .addOnSuccessListener {
                    cn.resume(FirebaseResponse.Success("Berhasil menambah galeri"))
                }
        }
}