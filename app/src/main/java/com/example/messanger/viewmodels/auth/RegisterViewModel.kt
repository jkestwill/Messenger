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
import javax.inject.Inject

class RegisterViewModel @Inject constructor(
    private var authRepository: AuthRepository
) : ViewModel() {
    private var _userLiveData = MutableLiveData<State<FirebaseUser>>()
    val userLiveData: LiveData<State<FirebaseUser>> get() = _userLiveData

    private val TAG = "RegisterViewModel"
    private val disposable = CompositeDisposable()

    init {
        if (authRepository.firebaseAuth.currentUser != null) {
            _userLiveData.value = Success(authRepository.firebaseAuth.currentUser!!)
        }
    }

    // пока ничего другого придумать по поводу обработки ошибок не могу
    // мб на сервере что то поменять
    fun register(username: String, email: String, password: String, repeatPassword: String) {
        if (password.length >= 6) {
            if (repeatPassword == password) {
                if (username.length >= 4) {
                    _userLiveData.value = Loading()
                    disposable.add(
                        authRepository.register(username, email, password)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({
                                _userLiveData.postValue(Success(it))
                                updateDeviceToken(it)

                            }, {
                                _userLiveData.value = Error(it.message!!)
                            })
                    )
                } else {
                    _userLiveData.value = Error("The length of the username must be longer than 4")
                }
            } else {
                _userLiveData.value = Error("Passwords are not equals")
            }
        } else {
            _userLiveData.value = Error("The length of the password must be longer than 6")
        }
    }

    private fun updateDeviceToken(firebaseUser: FirebaseUser) {
        disposable.add(
            authRepository.updateDeviceToken(firebaseUser)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e(TAG, "login: ok")
                }, {

                })
        )
    }


    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}