package com.example.messanger.repository

import com.example.messanger.models.CurrentUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
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
                    if(task.isSuccessful){
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
                    it.result?.let {result->
                        subscriber.onSuccess(result.user)
                    }

                }
                .addOnFailureListener {
                    subscriber.onError(it)
                }
        }
    }


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


}