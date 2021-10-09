package com.example.messanger.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.messanger.models.Error
import com.example.messanger.models.State
import com.example.messanger.models.Success
import com.example.messanger.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class RegisterViewModel @Inject constructor(
    var application: Application,
    var authRepository: AuthRepository
) : ViewModel() {

    val userLiveData = MutableLiveData<State<FirebaseUser>>()

    private val TAG = "RegisterViewModel"
    private val disposable = CompositeDisposable()

    init {

        if (authRepository.firebaseAuth.currentUser != null) {
            userLiveData.value = Success(authRepository.firebaseAuth.currentUser!!)
        }

    }


    fun register(username: String, email: String, password: String, repeatPassword: String) {
        if ( repeatPassword == password) {
            disposable.add(
                authRepository.register(username, email, password)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        userLiveData.value = Success(it)
                    disposable.add( authRepository.updateDeviceToken(it)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                Log.e(TAG, "login: ok")
                            }, {

                            })
                    )

                    }, {
                        userLiveData.value = Error(it.message!!)
                    })
            )
        }
    }



    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}