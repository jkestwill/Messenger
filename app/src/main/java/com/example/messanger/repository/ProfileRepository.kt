package com.example.messanger.repository

import android.util.Log
import com.example.messanger.models.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.lang.Exception

import javax.inject.Inject

class ProfileRepository @Inject constructor(
    var firebaseAuth: FirebaseAuth,
    var firebaseDatabase: FirebaseDatabase
) {
    private val TAG = "ProfileRepository"


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



    fun logout() {
        firebaseAuth.signOut()
    }

    fun removeDeviceToken(): Completable {
        return Completable.create { emitter ->
            firebaseDatabase.getReference("/users")
                .child(CurrentUser.user.uid)
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

    fun messagesCount(): Observable<Int> {
        return Observable.create { emitter ->
            firebaseDatabase.getReference("/messages")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var messageCount = 0
                        for (i in CurrentUser.user.messages.values) {
                            val genericTypeIndicator =
                                object : GenericTypeIndicator<HashMap<String, Message>>() {}
                            snapshot.child(i).getValue(genericTypeIndicator)?.let { messages ->
                                val item =
                                    (messages.values.toList()).filter { !it.isRead && it.to == CurrentUser.user.uid }
                                        .sortedWith { o1, o2 ->
                                            if ((o1.timestamp as Long) > (o2.timestamp as Long)) {
                                                -1
                                            } else if ((o1.timestamp as Long) == (o2.timestamp as Long)) {
                                                0
                                            } else 1
                                        }
                                if(item.isNotEmpty()){
                                    messageCount++
                                }

                            }
                        }
                        emitter.onNext(messageCount)

                    }

                    override fun onCancelled(error: DatabaseError) {
                        emitter.tryOnError(error.toException())
                    }
                })

        }
    }


    fun notificationCount(): Observable<Int> {
        return Observable.create{emitter->
            firebaseDatabase.getReference("/users")
                .child(CurrentUser.user.uid)
                .child("/friend_request")
                .addValueEventListener(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val typeIndicator=object :GenericTypeIndicator<HashMap<String, FriendRequest>>(){}
                        snapshot.getValue(typeIndicator)?.let {
                            emitter.onNext(it.size)
                        }?:emitter.onNext(0)
                    }
                    override fun onCancelled(error: DatabaseError) {
                        emitter.onError(error.toException())
                    }

                })
        }
    }
}