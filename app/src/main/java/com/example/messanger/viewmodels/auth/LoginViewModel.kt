package com.example.messanger.viewmodels.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.messanger.models.Error
import com.example.messanger.models.Loading
import com.example.messanger.models.State
import com.example.messanger.models.Success
import com.example.messanger.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LoginViewModel
@Inject constructor(
    private var authRepository: AuthRepository
) : ViewModel() {
    private val TAG = "LoginViewModel"
    private val disposable = CompositeDisposable()

    private var _userLiveData =
        MutableLiveData<State<FirebaseUser?>>(Success(authRepository.firebaseAuth.currentUser))
    val userLiveData: LiveData<State<FirebaseUser?>> get() = _userLiveData



    fun login(email: String, password: String) {
        if (password.length >= 6) {
            _userLiveData.value = Loading()
            disposable.add(
                authRepository.login(email, password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        updateDeviceToken(it)
                    }, {
                        _userLiveData.postValue(Error(it.message!!))
                    })
            )
        } else {
            _userLiveData.value = Error("The password length must be greater than or equal to 6 ")
        }
    }


    private fun updateDeviceToken(firebaseUser: FirebaseUser) {
        disposable.add(
            authRepository.updateDeviceToken(firebaseUser)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _userLiveData.postValue(Success(firebaseUser))
                }, {

                })
        )
    }



    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

}