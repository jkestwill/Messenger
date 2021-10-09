package com.example.messanger.repository


import android.util.Log
import com.example.messanger.models.*
import com.google.firebase.database.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.lang.NullPointerException


import javax.inject.Inject


class UsersRepository @Inject constructor(
    private var firebaseDataBase: FirebaseDatabase,

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
                        }?:Log.e(TAG, "onDataChange: yayitsa")
                    }

                    override fun onCancelled(error: DatabaseError) {
                        emitter.tryOnError(error.toException())

                    }

                })

        }
    }

    fun addFriendRequest(user: User): Completable {
        return Completable.create { emitter ->
            firebaseDataBase.getReference("/users")
                .child("/${user.uid}")
                .child("/friends")
                .child("/${CurrentUser.user.uid}")
                .setValue(false)
                .addOnCompleteListener {
                    emitter.onComplete()
                }
                .addOnFailureListener {
                    emitter.onError(it)
                }
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

    fun removeFriendRequest(userId: String): Completable {
        return Completable.create { emitter ->
            firebaseDataBase
                .getReference("/users")
                .child("/${userId}")
                .child("/friend_request")
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