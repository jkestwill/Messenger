package com.example.messanger.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.messanger.models.CurrentUser
import com.example.messanger.models.Error
import com.example.messanger.models.State
import com.example.messanger.models.Success
import com.example.messanger.models.User
import com.example.messanger.repository.ProfileRepository
import com.example.messanger.repository.UsersRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private var profileRepository: ProfileRepository,
    private var usersRepository: UsersRepository,
) : ViewModel() {

    var userLiveData = MutableLiveData<State<User>?>()
    val messageCountLiveDate = MutableLiveData<Int>()
    val notificationCountLiveData=MutableLiveData<Int>()
    private val disposable = CompositeDisposable()
    private val TAG = "ProfileViewModel"


    fun getCurrentUser() {
        disposable.add(
            profileRepository.getCurrentUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e(TAG, "getCurrentUser:$it")
                    userLiveData.postValue(Success(it))
                }, {
                    Log.e(TAG, "getCurrentUser: ${it.message}")
                    userLiveData.postValue(Error(it.message.toString()))
                })
        )
    }


    fun removeDeviceToken() {
        Log.e(TAG, "hui ${CurrentUser.user.deviceToken}")
        disposable.add(
            profileRepository.removeDeviceToken()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    CurrentUser.clear()
                    Log.e(TAG, "device_token removed")


                }, {
                    Log.e(TAG, "logout: $it")
                })
        )
    }

    fun logout() {
        Completable.create {
            profileRepository.logout()
            it.onComplete()
        }
            .observeOn(AndroidSchedulers.mainThread())
            .delay(100, TimeUnit.MILLISECONDS)
            .subscribe {
                Log.e(TAG, "logout: ")
            }

    }

    fun messageCount() {
       disposable.add(profileRepository.messagesCount()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                messageCountLiveDate.value = it


            }, {

            })
       )
    }

    fun notificationCount() {
       disposable.add( profileRepository.notificationCount()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                notificationCountLiveData.value = it

            }, {
                Log.e(TAG, "notificationCount: ${it.message}")
            })
       )
    }


    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

}