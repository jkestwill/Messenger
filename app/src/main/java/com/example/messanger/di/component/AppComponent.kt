package com.example.messanger.di.component

import android.app.Application
import com.example.messanger.di.module.*
import com.example.messanger.ui.screens.*
import com.example.messanger.ui.screens.profile.PhotosFragment
import com.example.messanger.ui.screens.profile.ProfileFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ViewModelFactoryModule::class,
        AuthViewModelModule::class,
        ViewModelModule::class,
        FirebaseModule::class,
        RepositoryModule::class
    ]
)
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun app(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(activity: MainActivity)

    fun inject(fragment: ProfileFragment)
    fun inject(fragment:RegisterFragment)
    fun inject(fragment:LoginFragment)
    fun inject(fragment: UserFragment)
    fun inject(fragment: NotificationFragment)
    fun inject(fragment:MessengerFragment)
    fun inject(fragment:MessageFragment)
    fun inject(fragment:FriendsFragment)
    fun inject(fragment: EditProfileFragment)
    fun inject(fragment:ProfileInfoFragment)
    fun inject(fragment:PrivateInfoFragment)
    fun inject(fragment:ForgotPasswordFragment)
    fun inject(fragment:PhotosFragment)

}