package com.example.messanger.models


import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName
import java.io.Serializable

@IgnoreExtraProperties
data class User(

    @set: PropertyName("uid")
    var uid: String = "",

    @set: PropertyName("photoUrl")
    var photoUrl: String = "",

    @set: PropertyName("username")
    var username: String = "",

    @set: PropertyName("email")
    var email: String = "",

    @set: PropertyName("friend_request")
    @get: PropertyName("friend_request")
    var friendRequest: HashMap<String, FriendRequest> = HashMap(),

    @set: PropertyName("friends")
    var friends: HashMap<String, Boolean> = HashMap(),

    @set: PropertyName("messages")
    var messages: HashMap<String, String> = HashMap(),

    @set: PropertyName("device_token")
    @get: PropertyName("device_token")
    var deviceToken:String=""
) : Serializable
