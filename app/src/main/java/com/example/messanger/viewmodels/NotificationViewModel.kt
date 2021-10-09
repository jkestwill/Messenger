package com.example.messanger.viewmodels


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.messanger.models.CurrentUser
import com.example.messanger.models.Error
import com.example.messanger.models.FriendRequest
import com.example.messanger.models.State
import com.example.messanger.models.Success
import com.example.messanger.repository.NotificationRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class NotificationViewModel
@Inject
constructor(private val notificationRepository: NotificationRepository) : ViewModel() {

    val notificationLiveData = MutableLiveData<State<MutableCollection<FriendRequest>>>()
    private val TAG = "NotificationViewModel"


    fun receiveNotification(){
        notificationRepository.receiveNotification()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                notificationLiveData.value=Success(it)
            },{
                notificationLiveData.value=Error("Error")
            })

    }


    fun acceptFriendRequest(userIdFrom: String){
        notificationRepository.acceptFriendRequest(userIdFrom,CurrentUser.user.uid)
            .mergeWith(notificationRepository.removeFriendRequest(userIdFrom, CurrentUser.user.uid))
            .mergeWith ( notificationRepository.acceptFriendRequest(CurrentUser.user.uid,userIdFrom))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

            },{

            })

    }

    fun declineFriendRequest(userIdFrom: String){
        notificationRepository.removeFriendRequest(userIdFrom,CurrentUser.user.uid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

            },{

            })
    }



}