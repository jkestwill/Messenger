package com.example.messanger.services.notification

import com.example.messanger.models.NotificationRequest
import io.reactivex.rxjava3.core.Completable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApi {
   companion object {
        const val BASE_URL="https://fcm.googleapis.com/"
    }

    @Headers(
        "Authorization:key=AAAAFqo1AlQ:APA91bFyyTOF7dikqYE4jZRssVNNeNEj8yAmrge42PJVIGawx1I0eGNkyWPSCacwIHqyBxmV5Z05FBjGDCHeb8mADwY68tqdlPCFrC0r74ac9yuvPNCvWvaxhWd0ABQfDzB4TedjUC9O",
        "Content-Type:application/json"
    )
    @POST("fcm/send")
    fun sendChatNotification(@Body notificationRequest:NotificationRequest): Completable
}