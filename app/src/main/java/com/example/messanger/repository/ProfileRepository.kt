package com.example.messanger.repository

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import android.util.Log
import androidx.core.graphics.decodeBitmap
import com.example.messanger.models.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject


class ProfileRepository @Inject constructor(
    var firebaseAuth: FirebaseAuth,
    var firebaseDatabase: FirebaseDatabase
) {
    private val TAG = "ProfileRepository"

    // находит юзера по id и получает его данные
    fun getCurrentUser(): Observable<User> {
        return Observable.create { emmiter ->
            firebaseAuth.currentUser?.let {
                firebaseDatabase.getReference("/users").child(
                    it.uid
                ).addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(User::class.java)
                        Log.e(TAG, "onDataChange: ${snapshot.getValue(User::class.java)}")
                        user?.let {
                            CurrentUser.user.apply {
                                uid = it.uid
                                email = it.email
                                username = it.username
                                photoUrl = it.photoUrl
                                friends = it.friends
                                messages = it.messages
                                deviceToken = it.deviceToken
                            }
                            emmiter.onNext(user)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        emmiter.tryOnError(error.toException())
                    }

                })
            }

        }
    }


    fun updatePhoto(photoUrl: String): Completable {
        return Completable.create { emitter ->
            CurrentUser.user.uid.let {
                firebaseDatabase.getReference("/users")
                    .child(it)
                    .child("/photoUrl")
                    .setValue(photoUrl)
                    .addOnCompleteListener {
                        CurrentUser.user.photoUrl = photoUrl
                        emitter.onComplete()
                    }
                    .addOnFailureListener { error ->
                        emitter.tryOnError(error)
                    }
            }
        }
    }


    fun updateStatus(status: String): Completable {
        return Completable.create { emitter ->
            CurrentUser.user.uid.let {
                firebaseDatabase.getReference("/users")
                    .child(it)
                    .child("/status")
                    .setValue(status)
                    .addOnCompleteListener {
                        emitter.onComplete()
                    }
                    .addOnFailureListener { error ->
                        emitter.tryOnError(error)
                    }
            }
        }
    }


    // при выходе с аккаунта удаляется device token
    fun removeDeviceToken(): Completable {
        return Completable.create { emitter ->
            firebaseAuth.currentUser?.uid?.let { uid ->
                firebaseDatabase.getReference("/users")
                    .child(uid)
                    .child("/device_token")
                    .removeValue()
                    .addOnCompleteListener {
                        emitter.onComplete()

                    }
                    .addOnFailureListener {
                        emitter.tryOnError(it)
                    }
            }
        }
    }

    fun getImageSize(uri: Uri, context: Context): Single<Photo> {
        return Single.create<Photo> { emitter ->
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            val bitmap = ImageDecoder.decodeBitmap(source)
            Log.e(TAG, "getImageSize: ${bitmap.height}")
            Log.e(TAG, "getImageSize: ${bitmap.width}")
            if(bitmap.width==0 || bitmap.height==0){
                emitter.onError(Exception("Image width or height is 0"))
            }
            else{
                emitter.onSuccess(Photo("", "", bitmap.width, bitmap.height, 0))
            }
        }
    }


    fun addPhotoToList(photo: Photo): Single<Photo> {
        return Single.create<Photo> { emitter ->
            Log.e(TAG, "addPhotoToList: ${firebaseAuth.currentUser?.uid}", )
            firebaseAuth.currentUser?.uid?.let { userId ->
                firebaseDatabase.getReference("/photos")
                    .child("/$userId")
                    .child("/${photo.id}")
                    .setValue(photo)
                    .addOnCompleteListener {
                        emitter.onSuccess(photo)
                        Log.e(TAG, "addPhotoToList: complete")
                    }
                    .addOnFailureListener {
                        emitter.tryOnError(it)
                        Log.e(TAG, "addPhotoToList:error ${it} ", )
                    }
            }
        }
    }


    fun getAllPhotosUrl(): Single<State<List<Photo>>> {
        return Single.create { emitter ->
            firebaseAuth.currentUser?.uid?.let { uid ->
                firebaseDatabase.getReference("/photos")
                    .child(uid)
                    .orderByChild("/uploadDate")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val genericTypeIndicator =
                                object : GenericTypeIndicator<HashMap<String, Photo>>() {}
                            snapshot.getValue(genericTypeIndicator)?.let {
                                emitter.onSuccess(Success(it.values.toList()))
                            }

                        }

                        override fun onCancelled(error: DatabaseError) {
                            emitter.tryOnError(error.toException())
                        }
                    })
            }
        }
    }


    fun logout() {
        firebaseAuth.signOut()
    }

}