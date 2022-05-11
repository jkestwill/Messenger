package com.example.messanger.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.messanger.models.*
import com.example.messanger.repository.NotificationRepository
import com.example.messanger.repository.UsersRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ServerValue
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class UsersViewModel
@Inject
constructor(
    private var usersRepository: UsersRepository,
    private var notificationRepository: NotificationRepository,
    private var firebaseAuth: FirebaseAuth
) : ViewModel() {
    private val TAG = "UsersViewModel"

    init {
        Log.e(TAG, ": ${firebaseAuth.currentUser?.displayName}")
    }

    private var _usersLiveData = MutableLiveData<State<List<User>>>()
    val usersLiveData: LiveData<State<List<User>>> get() = _usersLiveData


    private var _friendsLiveData = MutableLiveData<State<List<User>>>()
    val friendsLiveData:LiveData<State<List<User>>> get() = _friendsLiveData


    private val disposable = CompositeDisposable()


    fun getAllUsers() {
        disposable.add(
            usersRepository.users()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .map {
                    it.sortedWith { o1, o2 ->
                        o1.username.compareTo(o2.username)
                    }
                }
                .subscribe({
                    _usersLiveData.postValue(Success(it))
                    Log.e(TAG, "getAllUsers: ")
                }, {
                    _usersLiveData.postValue(Error("Error"))
                    Log.e(TAG, "${it.message.toString()} sukla")
                })
        )
    }

    fun getFriends() {
        firebaseAuth.currentUser?.let { currentUser ->
            disposable.add(usersRepository.users()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .map {
                    it.filter { user -> user.friends.containsKey(currentUser.uid) }
                }
                .map {
                    it.filter { user -> user.friends[currentUser.uid]!! }
                }
                .map {
                    it.sortedWith { o1, o2 ->
                        o1.username.compareTo(o2.username)
                    }
                }
                .subscribe({
                    _friendsLiveData.postValue(Success(it))
                    Log.e(TAG, "getFriends:usid ${currentUser.uid}, ${it}")
                },
                    {
                        _friendsLiveData.postValue(Error("Error"))
                    }
                )
            )
        }
    }


    fun addFriendRequest(user: User) {
        firebaseAuth.currentUser?.let { currentUser ->
            disposable.add(
                usersRepository.addFriendRequest(user, currentUser.uid).mergeWith(
                    notificationRepository.sendFriendRequest(
                        FriendRequest(
                            currentUser.uid,
                            "${currentUser.displayName} wants to add you as a friend",
                            ServerValue.TIMESTAMP
                        ),
                        user.uid
                    )
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({

                    }, {
                        Log.e(TAG, "addFriendRequest: ${it.message}")
                    })

            )
        }

    }


    fun removeFriend(userId: String) {
        firebaseAuth.currentUser?.let { currentUser ->
            disposable.add(
                usersRepository.remove(
                    userId, currentUser.uid
                )
                    .concatWith(usersRepository.remove(CurrentUser.user.uid, userId))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        Log.e(TAG, "removed:friend")
                    }, {
                        Log.e(TAG, "removeFriend: $it")
                    }
                    )
            )
        }
    }


    fun removeFriendRequest(userId: String) {
        firebaseAuth.currentUser?.let {currentUser->
            disposable.add(usersRepository.removeFriendRequest(userId,currentUser.uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e(TAG, "removed:friendRequest")
                }, {
                    Log.e(TAG, "removeFriendRequest: $it")
                }
                )
            )
        }
    }

    fun sendFriendRequestNotification(notification: NotificationRequest) {
        notificationRepository.sendNotification(notification)
    }


    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }


}