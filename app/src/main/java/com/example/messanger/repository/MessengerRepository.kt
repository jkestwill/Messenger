package com.example.messanger.repository

import android.util.Log
import com.example.messanger.models.CurrentUser
import com.example.messanger.models.Message
import com.example.messanger.models.MessagePreview
import com.example.messanger.models.User
import com.example.messanger.other.RandomString
import com.google.firebase.database.*
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

class MessengerRepository @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
    private val TAG = "MessengerRepository"

    fun createChatReference(
        userId: String,
        userIdFrom: String,
        chatReference: String
    ): Completable {
        return Completable.create { emitter ->
            firebaseDatabase.getReference("/users")
                .child("/$userId")
                .child("/messages")
                .child("/$userIdFrom")
                .setValue(chatReference)
                .addOnCompleteListener {
                    emitter.onComplete()
                }
                .addOnFailureListener {
                    emitter.onError(it)
                }
        }
    }

    fun observeUserChanges(userId: String): Observable<User> {
        Log.e("ebannay za;upa", "onDataChange${userId}: ")
        return Observable.create { emitter ->
            firebaseDatabase.getReference("/users")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.child("/$userId").getValue(User::class.java)?.let {
                            emitter.onNext(it)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        emitter.tryOnError(error.toException())
                    }


                })
        }
    }

    fun sendMessage(
        message: Message,
        chatReference: String,
    ): Completable {
        return Completable.create { emitter ->
            Log.e(TAG, "sendMessage: $chatReference")
            firebaseDatabase.getReference("messages")
                .child("/$chatReference")
                .child(message.id)
                .setValue(message)
                .addOnCompleteListener {
                    emitter.onComplete()
                }
                .addOnFailureListener {
                    emitter.tryOnError(it)
                }
        }
    }

    fun updateMessage(
        chatReference: String,
        message: Message
    ): Completable {
        return Completable.create { emmiter ->
            firebaseDatabase.getReference("/messages")
                .child("/$chatReference")
                .child("/${message.id}")
                .child("/isRead")
                .setValue(true)
                .addOnCompleteListener {
                    emmiter.onComplete()
                }
                .addOnFailureListener {
                    emmiter.tryOnError(it)
                    Log.e("qwe", "updateMessage: qwe")
                }
        }
    }


    fun getMessages(chatReference: String): Observable<List<Message>> {
        return Observable.create { emitter ->
            firebaseDatabase.getReference("/messages")
                .child(chatReference)
                .orderByChild("/timestamp")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val genericTypeIndicator =
                            object : GenericTypeIndicator<HashMap<String, Message>>() {}

                        snapshot.getValue(genericTypeIndicator)?.let {
                            emitter.onNext(it.values.toList().sortedWith { o1, o2 ->
                                if ((o1.timestamp as Long) > (o2.timestamp as Long)) {
                                    1
                                } else if ((o1.timestamp as Long) == (o2.timestamp as Long)) {
                                    0
                                } else -1
                            }

                            )
                        }
                        Log.e(TAG, "onDataChange: ${snapshot.getValue(genericTypeIndicator)}")
                    }

                    override fun onCancelled(error: DatabaseError) {
                        emitter.tryOnError(error.toException())
                    }

                })
        }
    }

    fun getLastMessages(): Observable<List<MessagePreview>> {
        return Observable.create { emitter ->
            firebaseDatabase.getReference("/messages")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val messagePreview = mutableListOf<MessagePreview>()
                        for (i in CurrentUser.user.messages.values) {
                            val genericTypeIndicator =
                                object : GenericTypeIndicator<HashMap<String, Message>>() {}

                            // показывает поселднее сообщение
                            snapshot.child(i).getValue(genericTypeIndicator)?.let {
                                val item = (it.values.toList()).sortedWith { o1, o2 ->
                                    if ((o1.timestamp as Long) > (o2.timestamp as Long)) {
                                        -1
                                    } else if ((o1.timestamp as Long) == (o2.timestamp as Long)) {
                                        0
                                    } else 1
                                }[0]

                                messagePreview.add(
                                    MessagePreview(
                                        i,
                                        item.fromId,
                                        "",
                                        "",
                                        CurrentUser.user.photoUrl,
                                        item.text,
                                        item.isRead,
                                        item.timestamp
                                    )
                                )

                            }

                        }
                        emitter.onNext(messagePreview)
                        Log.e(TAG, "onDataChange: Success")
                    }

                    override fun onCancelled(error: DatabaseError) {
                        emitter.tryOnError(error.toException())
                    }

                })
        }
    }

    fun getUserById(): Single<List<User>> {
        return Single.create { emitter ->
            firebaseDatabase.getReference("/users")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = mutableListOf<User>()
                        for (i in CurrentUser.user.messages.keys) {
                            val username =
                                snapshot.child(i).child("/username").getValue(String::class.java)
                                    ?: ""
                            val photoUrl =
                                snapshot.child(i).child("/photoUrl").getValue(String::class.java)
                                    ?: ""
                            user.add(User(i, photoUrl, username))
                        }
                        emitter.onSuccess(user)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        emitter.tryOnError(error.toException())
                    }

                })
        }
    }


}