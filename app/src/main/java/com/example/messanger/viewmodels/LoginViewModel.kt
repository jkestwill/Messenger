package com.example.messanger.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.messanger.models.Error
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
    private val disposable=CompositeDisposable()
     val userLiveData:MutableLiveData<State<FirebaseUser?>> = MutableLiveData(Success(authRepository.firebaseAuth.currentUser))



    fun login(email: String, password: String) {

            disposable.add(
                authRepository.login(email, password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                       disposable.add(authRepository.updateDeviceToken(it)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                userLiveData.postValue(Success(it))
                            }, {

                            })
                       )
                    }, {
                        userLiveData.value = Error(it.message!!)
                    })
            )
        }


    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

}