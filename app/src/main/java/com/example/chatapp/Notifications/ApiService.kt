package com.example.chatapp.Notifications

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @Headers(
        "Content-type:application/json",
        "Authorization:key=AAAAf0XAuBA:APA91bG54F8Nh-UWQGGUitm9T392SZvwQ140xz9BLzjy2ZeRI96REFjz7VCjUdJVGYzGqNLFpw7OTfXgW5EvR5tIkAbwOP0MOVHKTWo94ydgYEdlUGRt3GLPdvS8HxoJaWlN1tCtpml9"
    )
    @POST("fcm/send")
    fun sendNotification(@Body sender:Sender): Call<MyResponse>
}