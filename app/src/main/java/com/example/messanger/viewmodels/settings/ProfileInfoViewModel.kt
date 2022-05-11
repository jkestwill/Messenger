package com.example.messanger.viewmodels.settings

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.messanger.usecase.CurrentUserUseCase
import com.example.messanger.models.*
import com.example.messanger.other.DateFormat
import com.example.messanger.repository.EditProfileRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class ProfileInfoViewModel @Inject constructor(
    private var editProfileRepository: EditProfileRepository,
    private var currentUserUseCase: CurrentUserUseCase
) : ViewModel() {
    private val disposable = CompositeDisposable()

    private var _userLiveData= MutableLiveData<State<UserUi>?>()
    val userLiveData: LiveData<State<UserUi>?> get() = _userLiveData

    private val TAG = "ProfileInfoViewModel"

    fun getCurrentUser() {
        disposable.add(
            currentUserUseCase()
                .subscribe({
                    Log.e(TAG, "getCurrentUser: pizda")
                    _userLiveData.postValue(Success(it))
                }, {
                    Log.e(TAG, "getCurrentUserError: ${it}")
                    _userLiveData.value = Error(it.message.toString())
                })
        )
    }

    fun updateGender(gender: Gender){
        disposable.add(editProfileRepository.updateGender(gender.name)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe ({
                Log.e(TAG, "updateGender: complete")
            },{
                Log.e(TAG, "updateGender: $it")
            })
        )
    }

    fun updateBirthday(birthday:String){
        editProfileRepository.updateUserBirthday(DateFormat.toTimeStamp(birthday))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                Log.e(TAG, " updateBirthday: complete")
            },
                {
                    Log.e(TAG, " updateBirthday: ${it.message}")
                }
            )
    }

    fun updateUsername(username:String){
        if(username.isNotEmpty()) {
            disposable.add(editProfileRepository.updateUsername(username)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.e(TAG, "updateUsername: complete")
                },
                    {
                        Log.e(TAG, "updateUsername: ${it.message}")
                    }
                )
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}