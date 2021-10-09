package com.example.messanger.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import kotlin.reflect.KClass

@Singleton
class ViewModelProviderFactory
@Inject
constructor(
    private var creators: Map<Class<out ViewModel>,@JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        val creator: Provider<in ViewModel> = creators[modelClass] ?: creators.entries.firstOrNull {

            modelClass.isAssignableFrom(it.key)
        }?.value ?: throw IllegalArgumentException("unknown model class $modelClass")


        return creator.get() as T
    }

}