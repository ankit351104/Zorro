package com.example.zorro

import android.provider.ContactsContract.CommonDataKinds.Email
import android.widget.ImageView

class user {
    var name: String?=null
    var email: String?=null
    var UID: String?=null
    var imageURL: String?=null
    constructor(){}
    constructor(UID:String?,email:String, name:String?,imageURL:String?){
        this.name = name
        this.email = email
        this.UID = UID
        this.imageURL = imageURL
    }
}