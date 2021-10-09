package com.example.messanger.repository

import android.util.Log
import com.example.messanger.models.CurrentUser
import com.example.messanger.models.FriendRequest
import com.example.messanger.models.NotificationRequest
import com.example.messanger.services.notification.NotificationApi
import com.google.firebase.database.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

import javax.inject.Inject

class NotificationRepository
@Inject constructor (
    private var firebaseDatabase:FirebaseDatabase,
    private val api: NotificationApi
    ) {
    private val reference=firebaseDatabase.getReference("users")
    private  val TAG = "NotificationRepository"

    fun sendFriendRequest(friendRequest:FriendRequest, userUid:String):Completable {
      return  Completable.create{emitter->
            reference.child(userUid)
                .child("/friend_request")
                .child(CurrentUser.user.uid)
                .setValue(friendRequest)
                .addOnCompleteListener {
                    emitter.onComplete()
                }
                .addOnFailureListener {
                    emitter.onError(it)
                }
        }
    }

    fun receiveNotification(): Observable<MutableCollection<FriendRequest>> {
        return Observable.create{emitter->
            reference
                .child(CurrentUser.user.uid)
                .child("/friend_request")
                .orderByChild("/timestamp")
                .addValueEventListener(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val typeIndicator=object :GenericTypeIndicator<HashMap<String,FriendRequest>>(){}
                        snapshot.getValue(typeIndicator)?.let {
                            emitter.onNext(it.values)

                        }?:emitter.onNext(ArrayList())

                    }

                    override fun onCancelled(error: DatabaseError) {
                        emitter.onError(error.toException())
                    }

                })
        }
    }


    fun acceptFriendRequest(userUidFrom: String,userUidCurrent:String):Completable {
       return Completable.create{emitter->
           reference.child("/${userUidFrom}")
               .child("/friends")
               .child("/${userUidCurrent}")
               .setValue(true)
               .addOnSuccessListener {
                   emitter.onComplete()
               }
               .addOnFailureListener {
                   emitter.onError(it)
               }
        }



    }

    fun removeFriendRequest(userUidFrom: String, userUidCurrent:String):Completable{
        return Completable.create{emitter->
            reference.child("/${userUidCurrent}")
                .child("/friend_request")
                .child("/${userUidFrom}")
                .removeValue()
                .addOnCompleteListener {
                    emitter.onComplete()
                }
                .addOnFailureListener {
                    emitter.onError(it)
                }
        }

    }


    fun sendNotification(notification: NotificationRequest){
        api.sendChatNotification(notification)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e(TAG, "sendNotification: notification sended")
            },{
                Log.e(TAG, "sendNotification: $it")
            })

    }

}