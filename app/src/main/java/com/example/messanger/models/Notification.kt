package com.example.messanger.models


import com.google.firebase.database.PropertyName
import com.google.gson.annotations.SerializedName

sealed class Notification


data class FriendRequest(

    @SerializedName("title")
    @set: PropertyName("from")
    var from: String = "",

    @SerializedName("body")
    @set: PropertyName("title")
    var title: String? = "",

    @set: PropertyName("timestamp")
    var timestamp: Any? = null
):Notification()

data class MessageNotification(
    @SerializedName("title")
    var title:String="",
    @SerializedName("body")
    var body:String="",
    @SerializedName("tag")
    var tag:String=""

):Notification()



