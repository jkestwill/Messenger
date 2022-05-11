package com.example.messanger.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.messanger.repository.MainRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class MainViewModel @Inject constructor (private var mainRepository: MainRepository): ViewModel() {

    private var _messageCountLiveDate = MutableLiveData<Int>()
    val messageCountLiveDate: LiveData<Int> get()= _messageCountLiveDate

    private var _notificationCountLiveData= MutableLiveData<Int>()
    val notificationCountLiveData: LiveData<Int> get() = _notificationCountLiveData

    private var disposable=CompositeDisposable()
    fun getMessageCount(){
      disposable.add( mainRepository.messagesCount()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe ({
                _messageCountLiveDate.postValue(it)
            },{

            })
      )
    }

    fun notificationCount() {
        disposable.add( mainRepository.notificationCount()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                _notificationCountLiveData.value = it
            }, {
            })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}