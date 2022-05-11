package com.example.messanger.repository

import com.example.messanger.models.CurrentUser
import com.example.messanger.models.FriendRequest
import com.example.messanger.models.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class MainRepository @Inject constructor(
    private var firebaseDatabase: FirebaseDatabase,
    private var firebaseAuth: FirebaseAuth
) {

    // подсичтывается количество сообщений
    // для отображения во вкладке сообщения
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
                                    (messages.values.toList()).filter { !it.isRead && it.to == firebaseAuth.currentUser!!.uid }
                                        .sortedWith { o1, o2 ->
                                            if ((o1.timestamp as Long) > (o2.timestamp as Long)) {
                                                -1
                                            } else if ((o1.timestamp as Long) == (o2.timestamp as Long)) {
                                                0
                                            } else 1
                                        }
                                if (item.isNotEmpty()) {
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