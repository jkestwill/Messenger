package com.example.messanger.repository


import android.util.Log
import com.example.messanger.models.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.lang.NullPointerException


import javax.inject.Inject
import kotlin.Exception


class UsersRepository @Inject constructor(
    private var firebaseDataBase: FirebaseDatabase
) {
    private val TAG = "UserRepository"

    fun users(): Observable<List<User>> {
        return Observable.create { emitter ->
            firebaseDataBase.getReference("/users")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val genericTypeIndicator = object :
                            GenericTypeIndicator<HashMap<String, User>>() {}

                        snapshot.getValue(genericTypeIndicator)?.let {
                            emitter.onNext(it.values.toList())
                            Log.e(TAG, "onDataChange: $it")
                        } ?: Log.e(TAG, "onDataChange: yayitsa")
                    }

                    override fun onCancelled(error: DatabaseError) {
                        emitter.tryOnError(error.toException())

                    }

                })

        }
    }

    fun addFriendRequest(user: User, currentUserUid: String): Completable {
        return Completable.create { emitter ->
            firebaseDataBase.getReference("/users")
                .child("/${user.uid}")
                .child("/friends")
                .child("/${currentUserUid}")
                .setValue(false)
                .addOnCompleteListener {
                    emitter.onComplete()
                }
                .addOnFailureListener {
                    emitter.onError(it)
                }
        }
    }

    fun getUserById(userId: String): Single<User> {
       return Single.create<User>{emmitter->
            firebaseDataBase.getReference("/users")
                .child("/$userId")
                .addListenerForSingleValueEvent(object:ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val value =snapshot.getValue<User?>()
                        value?.let {
                            emmitter.onSuccess(it)
                        }?:emmitter.onError(Exception("User not found"))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        emmitter.onError(Exception("User not found"))
                    }

                })
        }
    }


    fun remove(userId: String, userFromUid: String): Completable {
        return Completable.create { emitter ->
            firebaseDataBase
                .getReference("/users")
                .child("/${userId}")
                .child("/friends")
                .child("/${userFromUid}")
                .removeValue()
                .addOnCompleteListener {
                    emitter.onComplete()
                }
                .addOnFailureListener {
                    emitter.onError(it)
                }
        }
    }

    fun removeFriendRequest(userId: String, currentUserUid: String): Completable {
        return Completable.create { emitter ->
            firebaseDataBase
                .getReference("/users")
                .child("/${userId}")
                .child("/friend_request")
                .child(currentUserUid)
                .removeValue()
                .addOnCompleteListener {
                    emitter.onComplete()
                }
                .addOnFailureListener {
                    emitter.onError(it)
                }

        }
    }


}