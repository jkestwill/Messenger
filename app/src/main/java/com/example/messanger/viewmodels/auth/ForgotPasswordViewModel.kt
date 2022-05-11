package com.example.messanger.viewmodels.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.messanger.models.Error
import com.example.messanger.models.State
import com.example.messanger.models.Success
import com.example.messanger.repository.AuthRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class ForgotPasswordViewModel @Inject constructor(private var authRepository: AuthRepository) :
    ViewModel() {
    private val disposable = CompositeDisposable()

    private var _forgotPasswordLiveData = MutableLiveData<State<String>>()
    val forgotPasswordLiveData: LiveData<State<String>> get() = _forgotPasswordLiveData

    fun forgotPassword(email: String) {
        if (email.isNotBlank() && email.contains("@")) {
            disposable.add(
                authRepository.forgotPassword(email)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        _forgotPasswordLiveData.postValue(Success("Password reset has been sent. Please check your email."))
                    }, {
                        _forgotPasswordLiveData.postValue(Error("Error"))
                    })
            )
        } else {
            _forgotPasswordLiveData.value = Error("Email is badly formatted")
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}