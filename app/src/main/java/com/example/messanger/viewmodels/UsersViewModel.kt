package com.example.messanger.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.messanger.models.*
import com.example.messanger.repository.NotificationRepository
import com.example.messanger.repository.UsersRepository
import com.google.firebase.database.ServerValue
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class UsersViewModel
@Inject
constructor(
    private val usersRepository: UsersRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    val usersLiveData = MutableLiveData<State<List<User>>>()
    val friendsLiveData = MutableLiveData<State<List<User>>>()


    private val disposable = CompositeDisposable()
    private val TAG = "FriendsViewModel"


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
                    usersLiveData.value = Success(it)
                    Log.e(TAG, "getAllUsers: ")
                }, {
                    usersLiveData.postValue(Error("Error"))
                    Log.e(TAG, "${it.message.toString()} sukla")
                })
        )
    }

    fun getFriends() {
        disposable.add(usersRepository.users()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .map {
                it.filter { user -> user.friends.containsKey(CurrentUser.user.uid) }
            }
            .map {
                it.filter { user -> user.friends[CurrentUser.user.uid] == true }
            }
            .map {
                it.sortedWith { o1, o2 ->
                    o1.username.compareTo(o2.username)
                }
            }
            .subscribe({
                friendsLiveData.value = Success(it)
                Log.e(TAG, "getFriends: ${it.isNotEmpty()}")
            },
                {
                    friendsLiveData.value = Error("Error")
                }
            )
        )
    }


    fun addFriendRequest(user: User) {
        disposable.add(
            usersRepository.addFriendRequest(user)
                .mergeWith(
                    notificationRepository.sendFriendRequest(
                        FriendRequest(
                            CurrentUser.user.uid,
                            "${CurrentUser.user.username} wants to add you as a friend",
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


    fun removeFriend(userId: String) {
        disposable.add(usersRepository.remove(userId, CurrentUser.user.uid)
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


    fun removeFriendRequest(userId: String) {
        disposable.add(usersRepository.removeFriendRequest(userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( {
                Log.e(TAG, "removed:friendRequest")
            },{
                Log.e(TAG, "removeFriendRequest: $it")
            }
            )
        )
    }

    fun sendFriendRequestNotification(notification:NotificationRequest){
        notificationRepository.sendNotification(notification)
    }


    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }


}