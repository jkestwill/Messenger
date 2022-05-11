package com.example.messanger.services.notification

import com.example.messanger.BuildConfig
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
        "Authorization:key=${BuildConfig.PUSH_API_KEY}",
        "Content-Type:application/json"
    )
    @POST("fcm/send")
    fun sendChatNotification(@Body notificationRequest:NotificationRequest): Completable
}