package com.example.chatapp.models

import java.io.Serializable

class GroupMessage:Serializable {
    var name:String? = null
    var haqida:String? = null


    constructor(name: String?, haqida: String?) {
        this.name = name
        this.haqida = haqida
//        this.userList = userList
    }

    constructor()


}