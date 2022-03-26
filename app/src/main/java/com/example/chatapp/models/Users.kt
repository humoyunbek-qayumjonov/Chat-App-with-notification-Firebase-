package com.example.chatapp.models

import java.io.Serializable

class Users:Serializable{
    var email:String? = null
    var displayName:String? = null
    var photoUrl:String? = null
    var uid:String? = null
    var userOnOf = true
    var time:String? = null
    var lastMessage:String? = null
    var token:String? = null

    constructor()
    constructor(
        email: String?,
        displayName: String?,
        photoUrl: String?,
        uid: String?,
        userOnOf: Boolean,
        time:String?,
        lastMessage: String?,
        token:String
    ) {
        this.email = email
        this.displayName = displayName
        this.photoUrl = photoUrl
        this.uid = uid
        this.userOnOf = userOnOf
        this.time = time
        this.lastMessage = lastMessage
        this.token = token
    }


}
