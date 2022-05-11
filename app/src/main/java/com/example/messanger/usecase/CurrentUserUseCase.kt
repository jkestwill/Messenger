package com.example.messanger.usecase

import android.util.Log
import com.example.messanger.models.UserUi
import com.example.messanger.repository.ProfileRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class CurrentUserUseCase @Inject constructor(private var profileRepository: ProfileRepository) {

    operator fun invoke():  Observable<UserUi> {
        return profileRepository.getCurrentUser()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {

                Log.e("usiauifjalsjflka", "invoke: ${ it.toUserUI().birthday}", )
                it.toUserUI()
            }
    }
}