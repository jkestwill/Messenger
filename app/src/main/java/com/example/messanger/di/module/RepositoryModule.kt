package com.example.messanger.di.module

import com.example.messanger.usecase.CurrentUserUseCase
import com.example.messanger.repository.ProfileRepository
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {

    @Provides
    fun providesCurrentUserUseCase(profileRepository: ProfileRepository): CurrentUserUseCase =
        CurrentUserUseCase(profileRepository)
}