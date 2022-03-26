package com.example.chatapp.models

class Message {
    var messageText:String? = null
    var date:String? = null
    var fromUid:String? = null
    var toUid:String? = null

    constructor()
    constructor(messageText: String?, date: String?, fromUid: String?, toUid: String?) {
        this.messageText = messageText
        this.date = date
        this.fromUid = fromUid
        this.toUid = toUid
    }


}