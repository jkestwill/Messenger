package com.example.messanger.models

data class MessagePreview(
    var id: String,
    var idFrom:String="",
    var userId:String="",
    var username:String="",
    var imageUrl: String = "",
    var lastMessage: String = "",
    var isRead:Boolean=false,
    var timestamp: Any?=null
)