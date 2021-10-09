package com.example.messanger.di.module

import androidx.lifecycle.ViewModel
import com.example.messanger.di.ViewModelKey
import com.example.messanger.viewmodels.*
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    abstract fun bindProfileViewModel(profileViewModel: ProfileViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UsersViewModel::class)
    abstract fun bindFriendsViewModel(usersViewModel: UsersViewModel):ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NotificationViewModel::class)
    abstract fun bindNotificationViewModel(notificationViewModel: NotificationViewModel):ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MessengerViewModel::class)
    abstract fun bindMessengerViewModel(messengerViewModel: MessengerViewModel):ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EditProfileViewModel::class)
    abstract fun bindEditProfileViewMode(editProfileViewModel: EditProfileViewModel):ViewModel
}
