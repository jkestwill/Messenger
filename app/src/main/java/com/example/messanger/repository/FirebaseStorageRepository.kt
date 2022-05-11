package com.example.messanger.repository

import android.net.Uri
import android.util.Log
import com.example.messanger.other.RandomString
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import io.reactivex.rxjava3.core.Single
import java.util.*
import javax.inject.Inject

class FirebaseStorageRepository @Inject constructor(firebaseStorage: FirebaseStorage) {

    private val reference = firebaseStorage.getReference("/images")

    private val TAG = "FirebaseStorageRepository"


    fun image(uri: Uri): Single<String> {
        return Single.create { emiter ->
            val filename = RandomString.generate(10)
            val imageTask = reference.child(filename)
                .putFile(uri)

            imageTask
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        reference.child(filename)
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