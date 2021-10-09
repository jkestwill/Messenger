package com.example.messanger.di

import androidx.lifecycle.ViewModel
import dagger.MapKey
import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy
import kotlin.reflect.KClass

@MustBeDocumented
@Target(allowedTargets = [AnnotationTarget.FUNCTION])
@Retention(value =  AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value : KClass<out ViewModel>)


