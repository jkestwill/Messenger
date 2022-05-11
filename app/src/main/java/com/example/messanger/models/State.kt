package com.example.messanger.models

sealed class State<T>()
class Success<T>(var result:T):State<T>()
class Error<T>(var message:String):State<T>()
class Loading<T>():State<T>()