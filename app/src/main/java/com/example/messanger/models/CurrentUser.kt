package com.example.messanger.models

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object CurrentUser {
    val user:User by lazy {
        User()
    }

    fun clear(){
        user.apply {
            uid=""
            username=""
            email=""
            photoUrl=""
            deviceToken=""
            friends= hashMapOf()
            messages= hashMapOf()
            birthday=123123124124
            gender=""

        }
    }
}