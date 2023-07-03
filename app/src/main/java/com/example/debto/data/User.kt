package com.example.debto.data

data class User(
    var username:String,
    var uid: String,
    var name:String,
    var friends: Set<String>?,
    var phone:String
)