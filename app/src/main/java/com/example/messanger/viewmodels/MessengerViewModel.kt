package com.example.messanger.viewmodels

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModel
import com.example.messanger.models.*
import com.example.messanger.other.DateFormat
import com.example.messanger.other.RandomString
import com.example.messanger.repository.MessengerRepository
import com.example.messanger.repository.NotificationRepository
import com.google.firebase.database.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import kotlin.math.abs

class MessengerViewModel
@Inject
constructor(
    private val messengerRepository: MessengerRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {
    private val TAG = "MessengerViewModel"


    val userLiveData = MutableLiveData<User>()
    val messagePreviewLiveData = MutableLiveData<List<MessagePreview>>()
    val lastMessageLiveDate = MutableLiveData<List<DateHeader<Message>>>()

    private val disposable = CompositeDisposable()

    fun observeUserChanges(userId: String) {
        disposable.add(messengerRepository.observeUserChanges(userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                createChatReference(it)

                userLiveData.value = it
                Log.e(TAG, "observeUserChanges: $it")
            }
        )
    }

    fun sendMessage(user: User, messageText: String) {
        val message = Message(
            RandomString.generate(10),
            user.uid,
            CurrentUser.user.uid,
            messageText,
            false,
            ServerValue.TIMESTAMP
        )

        user.messages[CurrentUser.user.uid]?.let {
            disposable.add(
                messengerRepository.sendMessage(message, it)

                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({

                    }, {

                    })
            )
            Log.e("message", it)
        }
    }


    fun getMessages(user: User) {
        user.messages[CurrentUser.user.uid]?.let {
            disposable.add(
                messengerRepository.getMessages(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { messages ->

                        val header = messages
                            .map { message ->

                                Item(message) as DateHeader<Message>
                            }
                            .toMutableList()
                        var i = 0
                        while (i < header.size) {
                            when (val item = header[i]) {
                                is Item -> {
                                    if (i - 1 < 0) {
                                        header.add(
                                            i,
                                            Date(DateFormat.formatDay((header[i] as Item).data.timestamp as Long))
                                        )
                                        i++
                                        continue
                                    }
//
                                    if (header[i - 1] is Item)
                                        if (
                                            DateFormat.day(item.data.timestamp as Long) !=
                                            DateFormat.day((header[i - 1] as Item).data.timestamp as Long)
                                        ) {
                                            header.add(
                                                i,
                                                Date(DateFormat.formatDay((header[i] as Item).data.timestamp as Long))
                                            )
                                            i++
                                        }
                                }
                            }

                            i++
                        }
                        header
                    }

                    .subscribe(
                        { message ->
                            if(message.isNotEmpty()) {
                                message.forEach { item ->
                                    when (item) {
                                        is Item -> {
                                            if(item.data.to==CurrentUser.user.uid) {
                                                messengerRepository.updateMessage(it, item.data)
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe({

                                                    }, { error ->
                                                        Log.e(TAG, "getMessages: ${error.message}",)
                                                    })
                                            }
                                        }
                                        is Date -> {

                                        }
                                    }
                                }
                            }
                            lastMessageLiveDate.value = message
                            Log.e(TAG, "getMessages: ${message}")
                        },
                        {
                            Log.e(TAG, "getMessages: $it")
                        },
                    )
            )
        }
    }


    private fun createChatReference(user: User) {
        var chatReference = RandomString.generate(15)
        if (!user.messages[CurrentUser.user.uid].isNullOrEmpty()) {
            chatReference = user.messages[CurrentUser.user.uid]!!
        }
        disposable.add(
            messengerRepository.createChatReference(user.uid, CurrentUser.user.uid, chatReference)
                .concatWith(
                    messengerRepository.createChatReference(
                        CurrentUser.user.uid,
                        user.uid,
                        chatReference
                    )
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                }, {
                    Log.e(TAG, "createChatReference: $it")
                })
        )
    }


    fun getMessagePreview() {
        disposable.add(messengerRepository.getLastMessages()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                Observable.fromIterable(it)
                    .zipWith(
                        messengerRepository.getUserById().flattenAsObservable { it },
                        { t1, t2 ->
                            t1.userId = t2.uid
                            t1.imageUrl = t2.photoUrl
                            t1.username = t2.username
                            t1
                        })
                    .toSortedList { o1, o2 ->
                        if ((o1.timestamp as Long) > (o2.timestamp as Long)) {
                            -1
                        } else if ((o1.timestamp as Long) == (o2.timestamp as Long)) {
                            0
                        } else 1
                    }
                    .toObservable()
            }


            .subscribe({message->
                messagePreviewLiveData.value = message
            }, {
                Log.e(TAG, "getMessagePreview: ${it.message}")
            })
        )
    }

    fun sendMessageNotification(notification: NotificationRequest) {
        notificationRepository.sendNotification(notification)
    }


    override fun onCleared() {
        super.onCleared()
        disposable.clear()

    }


}