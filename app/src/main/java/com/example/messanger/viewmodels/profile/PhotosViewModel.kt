package com.example.messanger.viewmodels.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.messanger.models.Photo
import com.example.messanger.models.State
import com.example.messanger.models.Success
import com.example.messanger.repository.ProfileRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class PhotosViewModel @Inject constructor(
    private var profileRepository: ProfileRepository
) : ViewModel() {

    private var _photos = MutableLiveData<State<List<Photo>>>()
     val photos : LiveData<State<List<Photo>>> get() = _photos

    private val TAG = "PhotosViewModel"
    private val disposable = CompositeDisposable()

    fun getAllPhotosUrl(photoId: String) {
        disposable.add(
            profileRepository.getAllPhotosUrl()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _photos.value=it
                    Log.e(TAG, "getAllPhotosUrl: ${it}")
                }, {
                    Log.e(TAG, "getAllPhotosUrl: $it", )
                })
        )
    }

}