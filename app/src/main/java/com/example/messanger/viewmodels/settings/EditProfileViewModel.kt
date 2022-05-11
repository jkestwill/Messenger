package com.example.messanger.viewmodels.settings

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.messanger.usecase.CurrentUserUseCase
import com.example.messanger.models.*
import com.example.messanger.repository.AuthRepository
import com.example.messanger.repository.EditProfileRepository
import com.example.messanger.repository.FirebaseStorageRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class EditProfileViewModel
@Inject constructor(
    private var editProfileRepository: EditProfileRepository,
    private var firebaseStorageRepository: FirebaseStorageRepository,
    private var authRepository: AuthRepository
) : ViewModel() {
    private val disposable = CompositeDisposable()
    private val TAG = "ProfileViewModel"

    private var _userLiveData = MutableLiveData<State<UserUi>?>()
    val userLiveData: LiveData<State<UserUi>?> get() = _userLiveData

    // отпраляет фото в бд
    fun sendImage(uri: Uri) {
        disposable.add(
            firebaseStorageRepository.image(uri)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e(TAG, "image sended ${it}")

                }, {

                })

        )
    }

    // отправляет ссылку фото в бд юзера
    private fun updatePhoto(photoUrl: String) {
        disposable.add(editProfileRepository.updatePhoto(photoUrl)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.e(TAG, "getImg: complete")
            }
        )
    }

    fun removeAccount(email: String, password: String) {
        disposable.add(
            authRepository.removeAccount(email, password)
                .mergeWith(authRepository.removeUser())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e(TAG, "remove acc complete")
                }, {
                    Log.e(TAG, "removeAccount: failure ")
                }
                )
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }


}