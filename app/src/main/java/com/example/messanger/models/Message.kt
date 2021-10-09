package com.example.messanger.models

import com.google.firebase.database.PropertyName


data class Message(
    var id:String="",
    var to: String = "",
    var fromId: String = "",
    var text: String = "",
    @get:PropertyName("isRead")
    @set:PropertyName("isRead")
    var isRead:Boolean=false,
    var timestamp:Any?=null
)