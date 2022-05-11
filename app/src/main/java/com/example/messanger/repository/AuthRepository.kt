package com.example.messanger.repository

import com.example.messanger.models.CurrentUser
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AuthRepository @Inject constructor(
    var firebaseAuth: FirebaseAuth,
    var firebaseDatabase: FirebaseDatabase,
    var firebaseMessaging: FirebaseMessaging
) {


    fun login(email: String, password: String): Single<FirebaseUser> {
        return Single.create { subscriber ->
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.user?.let {
                            subscriber.onSuccess(it)
                        }
                    }
                }
                .addOnFailureListener {
                    subscriber.tryOnError(it)

                }
        }
    }


    fun forgotPassword(email: String): Completable {
        return Completable.create { emmiter ->
            firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener {
                    emmiter.onComplete()
                }
                .addOnFailureListener {
                    emmiter.tryOnError(it)
                }


        }
    }

    // пофиксить юзера
    fun register(username: String, email: String, password: String): Single<FirebaseUser> {
        return Single.create { subscriber ->
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    firebaseAuth.currentUser?.let {
                        firebaseDatabase.getReference("/users").child(it.uid).apply {
                            child("/email").setValue(firebaseAuth.currentUser?.email)
                            child("/username").setValue(username)
                            child("/uid").setValue(firebaseAuth.currentUser?.uid)
                            CurrentUser.user.email = firebaseAuth.currentUser?.email.toString()
                            CurrentUser.user.username = username
                            CurrentUser.user.uid = firebaseAuth.currentUser!!.uid
                        }
                    }
                    it.result?.let { result ->
                        updateUsername(username)
                        subscriber.onSuccess(result.user)
                    }
                }
                .addOnFailureListener {
                    subscriber.onError(it)
                }


        }
    }

    private fun updateUsername(username: String) {
        firebaseAuth.currentUser?.updateProfile(
            UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build()
        )
    }

    // девайс токен нужен для того чтобы оперделить с какого устройства был выполнен вход
    // также нужен для отправки push-уведомлений
    fun updateDeviceToken(user: FirebaseUser): Completable {
        return Completable.create { emitter ->
            user.uid.let { user ->
                firebaseMessaging.token.addOnCompleteListener { token ->

                    firebaseDatabase.getReference("/users")
                        .child(user)
                        .child("/device_token")
                        .setValue(token.result)
                        .addOnCompleteListener {
                            if (token.isSuccessful) {
                                CurrentUser.user.deviceToken = token.result!!
                            }
                            emitter.onComplete()
                        }
                        .addOnFailureListener {
                            emitter.onError(it)
                        }
                }
            }
        }
    }

    fun removeAccount(email: String, password: String): Completable {
      return  Completable.create { emitter ->
            firebaseAuth.currentUser?.reauthenticate(
                EmailAuthProvider.getCredential(
                    email,
                    password
                )
            )?.addOnCompleteListener {
                firebaseAuth.currentUser?.delete()
                    ?.addOnCompleteListener {
                        emitter.onComplete()
                    }
                    ?.addOnFailureListener {
                        emitter.tryOnError(it)
                    }
            }?.addOnFailureListener {
                emitter.tryOnError(it)
            }
        }
    }


    fun removeUser(): Completable {
       return Completable.create { emitter ->
            firebaseAuth.currentUser?.uid?.let { uid ->
                firebaseDatabase.getReference("/users")
                    .child(uid)
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


}