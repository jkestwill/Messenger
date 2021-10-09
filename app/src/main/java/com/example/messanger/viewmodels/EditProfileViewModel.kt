package com.example.messanger.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.messanger.models.Error
import com.example.messanger.models.State
import com.example.messanger.models.Success
import com.example.messanger.models.User
import com.example.messanger.repository.EditProfileRepository
import com.example.messanger.repository.FirebaseStorageRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class EditProfileViewModel
@Inject constructor(
    private var editProfileRepository: EditProfileRepository,
    private var firebaseStorageRepository: FirebaseStorageRepository
) : ViewModel() {
    private val disposable = CompositeDisposable()
    private val TAG = "ProfileViewModel"

    var userLiveData = MutableLiveData<State<User>?>()

    fun sendImage(uri: Uri){
        disposable.add( firebaseStorageRepository.image(uri)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e(TAG,"image sended ${it}")

               disposable.add( editProfileRepository.updatePhoto(it.toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        Log.e(TAG, "getImg: complete")
                    }
               )
            },{

            })

        )
    }

    fun getCurrentUser(){
        disposable.add(  editProfileRepository.getCurrentUser()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e(TAG, "getCurrentUser: pizda")
                userLiveData.value=Success(it)
            },{
                Log.e(TAG, "getCurrentUser: ${it.message}")
                userLiveData.value=Error(it.message.toString())
            })
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

    fun updateEmail(email:String,password: String){
        if(email.isNotEmpty() && password.isNotEmpty()) {

            disposable.add(editProfileRepository.updateEmail(email,password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .concatWith(editProfileRepository.updateUserEmail(email))
                .subscribe({
                    Log.e(TAG, "updateEmail: complete")

                },
                    {
                        Log.e(TAG, "updateEmail: ${it.message}")
                    }
                )
            )
        }

    }


    fun updatePassword(newPassword:String,password:String,repeatPassword:String){
        if(password==repeatPassword && password.isNotEmpty() && newPassword.isNotEmpty() && newPassword!=password){
          disposable.add(editProfileRepository.updatePassword(newPassword,password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.e(TAG, " updatePassword: complete")
                },
                    {
                        Log.e(TAG, " updatePassword: ${it.message}")
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