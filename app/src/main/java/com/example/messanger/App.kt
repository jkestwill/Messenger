package com.example.messanger

import android.app.Application
import android.util.Log
import com.example.messanger.di.component.AppComponent
import com.example.messanger.di.component.DaggerAppComponent
import com.google.firebase.auth.ktx.FirebaseAuthKtxRegistrar
import com.google.firebase.database.ktx.database

import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.google.firebase.messaging.ktx.messaging
import com.google.firebase.storage.ktx.storage
import io.reactivex.rxjava3.exceptions.UndeliverableException
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import java.io.IOException
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.lang.NullPointerException
import java.net.SocketException

class App : Application() {
 lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(this)

        appComponent=DaggerAppComponent
            .builder()
            .app(this)
            .build()


    }


}