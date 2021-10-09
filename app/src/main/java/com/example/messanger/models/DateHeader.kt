package com.example.messanger.models

sealed class DateHeader<T>

data class Item<T>(var data:T):DateHeader<T>()

data class Date<T>(var date:String):DateHeader<T>()