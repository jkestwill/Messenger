package com.example.messanger.models


import com.example.messanger.other.DateFormat
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
    var deviceToken: String = "",

    @set: PropertyName("about_me")
    var aboutMe: String = "",
    @set: PropertyName("birthday")
    var birthday:Long = 0L,
    var gender: String="",
    var status:String=""
) : Serializable{

    fun toUserUI(): UserUi {
        val birthday= if(this.birthday==0L) "" else DateFormat.formatDay(this.birthday)
        val gender=if(gender.isBlank()) Gender.NONE else Gender.valueOf(this.gender)
       return UserUi(username,email, birthday,gender,status)
    }
}


