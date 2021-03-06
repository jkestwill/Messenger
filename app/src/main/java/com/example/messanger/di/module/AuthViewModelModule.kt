package com.example.messanger.di.module

import androidx.lifecycle.ViewModel
import com.example.messanger.di.ViewModelKey
import com.example.messanger.viewmodels.auth.LoginViewModel
import com.example.messanger.viewmodels.auth.RegisterViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class AuthViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey( LoginViewModel::class)
    abstract fun bindLoginViewModel(loginViewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey( RegisterViewModel::class)
    abstract fun bindRegisterViewModel(registerViewModel: RegisterViewModel): ViewModel
}