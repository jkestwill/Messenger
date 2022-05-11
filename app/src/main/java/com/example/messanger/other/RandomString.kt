package com.example.messanger.other

import kotlin.random.Random

object RandomString {

    @JvmStatic
    fun generate(size:Int): String {
        val charPool = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..size)
            .map { charPool[Random.nextInt(0,charPool.size-1)] }
            .joinToString ("")

    }
}