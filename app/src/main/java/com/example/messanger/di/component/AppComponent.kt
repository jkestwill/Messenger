package com.example.messanger.di.component

import android.app.Application
import com.example.messanger.di.module.*
import com.example.messanger.ui.screens.*
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ViewModelFactoryModule::class,
        AuthViewModelModule::class,
        MainViewModelModule::class,
        FirebaseModule::class,

    ]
)
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun app(application: Application): Builder

        fun build(): AppComponent
    }


    fun inject(fragment: ProfileFragment)
    fun inject(fragment:RegisterFragment)
    fun inject(fragment:LoginFragment)
    fun inject(fragment: UserFragment)
    fun inject(fragment: NotificationFragment)
    fun inject(fragment:MessengerFragment)
    fun inject(fragment:MessageFragment)
    fun inject(fragment:FriendsFragment)
    fun inject(fragment: EditProfileFragment)


}