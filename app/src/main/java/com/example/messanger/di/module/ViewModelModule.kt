package com.example.messanger.di.module

import androidx.lifecycle.ViewModel
import com.example.messanger.di.ViewModelKey
import com.example.messanger.viewmodels.*
import com.example.messanger.viewmodels.auth.ForgotPasswordViewModel
import com.example.messanger.viewmodels.profile.PhotosViewModel
import com.example.messanger.viewmodels.profile.ProfileViewModel
import com.example.messanger.viewmodels.settings.EditProfileViewModel
import com.example.messanger.viewmodels.settings.PrivateInfoViewModel
import com.example.messanger.viewmodels.settings.ProfileInfoViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

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

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindEditMainViewMode(viewModel: MainViewModel):ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProfileInfoViewModel::class)
    abstract fun bindProfileInfoViewModel(viewModel: ProfileInfoViewModel):ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PrivateInfoViewModel::class)
    abstract fun bindPrivateInfoViewModel(viewModel: PrivateInfoViewModel):ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ForgotPasswordViewModel::class)
    abstract fun bindForgotPasswordViewModel(viewModel:ForgotPasswordViewModel):ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PhotosViewModel::class)
    abstract fun bindPhotosViewModel(viewModel: PhotosViewModel):ViewModel
}
