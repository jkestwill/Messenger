package com.example.messanger.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class FirebaseStorageRepository @Inject constructor(firebaseStorage: FirebaseStorage) {

    private val reference = firebaseStorage.getReference("/images")

    private val TAG = "FirebaseStorageRepository"


    fun image(uri: Uri): Single<String> {
        val fileName = uri.path?.split("/")
        Log.e(TAG, "image: $fileName")
        return Single.create { emiter ->
            fileName?.get(fileName.size - 1)?.let {
                reference.child(it)
                    .putFile(uri)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            reference.child(it)
                                .downloadUrl
                                .addOnCompleteListener { downlTask ->
                                    emiter.onSuccess(downlTask.result.toString())
                                }
                            Log.e(TAG, "image: complete")
                        }
                    }
                    .addOnFailureListener { ex ->
                        emiter.onError(ex)
                    }
            }
        }
    }
}