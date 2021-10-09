package com.example.messanger.models

import com.google.gson.annotations.SerializedName

data class NotificationRequest(
    @SerializedName("to")
    var string: String?=null,
    @SerializedName("notification")
    var notification: Notification?=null,

) {
}