package com.example.messanger.other

import kotlin.random.Random

object RandomString {
    fun generate(size:Int): String {
        val charPool = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..size)
            .map { charPool.random()}
            .joinToString ("")

    }
}