package com.example.messanger.viewmodels.settings

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.messanger.models.Error
import com.example.messanger.models.Loading
import com.example.messanger.models.State
import com.example.messanger.models.Success
import com.example.messanger.repository.EditProfileRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class PrivateInfoViewModel @Inject constructor(
    private var editProfileRepository: EditProfileRepository
) : ViewModel() {
    private val disposable = CompositeDisposable()
    private val TAG = "PrivateInfoViewModel"

    private var _emailLiveData = MutableLiveData<State<String>>()
    val emailLiveData: LiveData<State<String>> get() = _emailLiveData

    private var _passwordLiveData = MutableLiveData<State<String>>()
    val passwordLiveData: LiveData<State<String>> get() = _passwordLiveData

    fun updateEmail(email: String, password: String) {
        _emailLiveData.value=Loading()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            disposable.add(editProfileRepository.updateEmail(email, password)
                .mergeWith(editProfileRepository.sendEmailVerification())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .concatWith(editProfileRepository.updateUserEmail(email))
                .subscribe({
                    Log.e(TAG, "updateEmail: complete")
                    _emailLiveData.postValue(Success("Complete"))
                },
                    {
                        _emailLiveData.postValue(Error("Error"))
                        Log.e(TAG, "updateEmail: ${it.message}")
                    }
                )
            )
        } else {
            _emailLiveData.postValue(Error("Password or email are empty"))
        }
    }



    fun updatePassword(newPassword: String, password: String, repeatPassword: String) {
        if (newPassword == repeatPassword && password.isNotEmpty() && newPassword.isNotEmpty() && newPassword != password) {
            _passwordLiveData.value=Loading()
            disposable.add(editProfileRepository.updatePassword(newPassword, password)
                .mergeWith(editProfileRepository.sendPasswordChangeEmail())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.e(TAG, " updatePassword: complete")
                    _passwordLiveData.value=Success("Password has been changed")
                },
                    {
                        _passwordLiveData.postValue(Error("Error"))
                        Log.e(TAG, " updatePassword: ${it.message}")
                    }
                )
            )
        }
        else{
            _passwordLiveData.value=Error("password error")
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}