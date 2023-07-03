package com.example.debto.data

data class Expense(
    val friend:String,
    val amount:Float,
    val comment:String,
    val time:Long,
    val iPaid:Boolean
)