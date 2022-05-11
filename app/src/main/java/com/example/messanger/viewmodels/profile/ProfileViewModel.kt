package com.example.messanger.viewmodels.profile

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.example.messanger.models.*
import com.example.messanger.other.DateFormat
import com.example.messanger.other.RandomString
import com.example.messanger.repository.FirebaseStorageRepository
import com.example.messanger.repository.ProfileRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private var profileRepository: ProfileRepository,
    private var firebaseStorageRepository: FirebaseStorageRepository,
    private var app: Application
) : AndroidViewModel(app) {

    private var _userLiveData = MutableLiveData<State<User>?>()
    val userLiveData: LiveData<State<User>?> get() = _userLiveData


    private var _imageLiveData = MutableLiveData<State<Photo>>()
    val imageLiveData: LiveData<State<Photo>?> get() = _imageLiveData

    private val disposable = CompositeDisposable()
    private val TAG = "ProfileViewModel"


    fun getCurrentUser() {
        _userLiveData.value = Loading()
        disposable.add(
            profileRepository.getCurrentUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e(TAG, "getCurrentUser:$it")
                    _userLiveData.postValue(Success(it))
                }, {
                    Log.e(TAG, "getCurrentUser: ${it.message}")
                    _userLiveData.postValue(Error(it.message.toString()))
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


    fun uploadImage(uri: Uri) {
        _imageLiveData.value = Loading()
        disposable.add(
            firebaseStorageRepository.image(uri)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .zipWith(
                    profileRepository.getImageSize(uri, app.applicationContext)
                ) { url, photo ->
                    photo.url = url

                    photo.id=RandomString.generate(10)

                    Log.e(TAG, "IDDDDDDDDD ${photo.id}", )
                    photo
                }
                .concatMap {
                    profileRepository.addPhotoToList(it)
                }
                .subscribe({
                    _imageLiveData.value = Success(it)
                    Log.e(TAG, "uploadImage: success ${it}")
                }, {
                    Log.e(TAG, "getImageSize: ${it.message}")
                    _imageLiveData.value = Error("Error")
                })
        )
    }

    fun updateProfilePhoto(photoUrl:String){
        disposable.add(
            profileRepository.updatePhoto(photoUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e(TAG, "updateProfilePhoto: success")
                },{
                    Log.e(TAG, "updateProfilePhoto:Error ")
                })
        )
    }


    fun updateStatus(newStatus: String) {
        disposable.add(
            profileRepository.updateStatus(newStatus)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e(TAG, "updateStatus: complete")
                }, {
                    Log.e(TAG, "updateStatus: fail")
                }
                )
        )
    }


    // делей из-за того что он сразу не выходит
    // тк. FirebaseUser не успевает занулиться
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


    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

}