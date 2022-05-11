package com.example.messanger.repository

import android.util.Log
import com.example.messanger.models.CurrentUser
import com.example.messanger.models.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class EditProfileRepository @Inject constructor(
    var firebaseAuth: FirebaseAuth,
    var firebaseDatabase: FirebaseDatabase
) {

    private val TAG = "EditProfileRepository"

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
                                gender = "sas"
                            }

                            emmiter.onNext(user)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(TAG, "onCancelled $error: ")
                        emmiter.tryOnError(error.toException())
                    }

                })
            }

        }
    }

    fun updatePassword(newPassword: String, password: String): Completable {
        return Completable.create { emitter ->
            firebaseAuth.currentUser?.email?.let { email ->
                val credential =
                    EmailAuthProvider.getCredential(email, password)
                firebaseAuth.currentUser?.reauthenticate(credential)
                    ?.addOnCompleteListener {
                        firebaseAuth.currentUser?.updatePassword(newPassword)
                            ?.addOnCompleteListener {
                                emitter.onComplete()
                            }
                            ?.addOnFailureListener {
                                emitter.tryOnError(it)
                            }

                    }
                    ?.addOnFailureListener {
                        emitter.tryOnError(it)
                    }

            }
        }
    }

    fun sendEmailVerification(): Completable {
        return Completable.create { emmiter ->
            firebaseAuth.currentUser?.sendEmailVerification()
                ?.addOnCompleteListener {
                    emmiter.onComplete()
                }
                ?.addOnFailureListener {
                    emmiter.tryOnError(it)
                }

        }
    }

    fun sendPasswordChangeEmail(): Completable {
        return Completable.create { emmiter ->
            firebaseAuth.currentUser?.email?.let { email ->
                firebaseAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener {
                        emmiter.onComplete()
                    }
                    .addOnFailureListener {
                        emmiter.tryOnError(it)
                    }
            }
        }
    }

    fun updateEmail(email: String, password: String): Completable {
        return Completable.create { emitter ->
            firebaseAuth.currentUser?.email?.let { currentEmail ->
                val credential =
                    EmailAuthProvider.getCredential(currentEmail, password)
                firebaseAuth.currentUser?.reauthenticate(credential)
                    ?.addOnCompleteListener {
                        firebaseAuth.currentUser?.updateEmail(email)
                            ?.addOnCompleteListener {
                                emitter.onComplete()
                                CurrentUser.user.email = email
                            }
                            ?.addOnFailureListener {
                                emitter.tryOnError(it)
                            }
                    }
                    ?.addOnFailureListener {
                        emitter.tryOnError(it)
                    }
            }

        }
    }

    fun updateGender(gender: String): Completable {
        return Completable.create { emitter ->
            firebaseAuth.currentUser?.let { currentUser ->
                firebaseDatabase.getReference("/users")
                    .child(currentUser.uid)
                    .child("/gender")
                    .setValue(gender)
                    .addOnCompleteListener {
                        emitter.onComplete()
                    }
                    .addOnFailureListener {
                        emitter.tryOnError(it)
                    }
            }
        }
    }

    fun updateUserBirthday(birthday: Long): Completable {
        return Completable.create { emitter ->
            firebaseAuth.currentUser?.let { currentUser ->
                firebaseDatabase.getReference("/users")
                    .child(currentUser.uid)
                    .child("/birthday")
                    .setValue(birthday)
                    .addOnCompleteListener {
                        emitter.onComplete()
                    }
                    .addOnFailureListener {
                        emitter.tryOnError(it)
                    }
            }
        }
    }

    fun updateUserEmail(email: String): Completable {
        return Completable.create { emitter ->
            firebaseAuth.currentUser?.let { currentUser ->
                firebaseDatabase.getReference("/users")
                    .child(currentUser.uid)
                    .child("/email")
                    .setValue(email)
                    .addOnCompleteListener {
                        emitter.onComplete()
                    }
                    .addOnFailureListener {
                        emitter.tryOnError(it)
                    }
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

    fun updateUsername(username: String): Completable {
        return Completable.create { emitter ->
            firebaseDatabase
                .getReference("/users")
                .child(CurrentUser.user.uid)
                .child("/username")
                .setValue(username)
                .addOnCompleteListener {
                    emitter.onComplete()
                }
                .addOnFailureListener { error ->
                    emitter.tryOnError(error)
                }

        }
    }
}