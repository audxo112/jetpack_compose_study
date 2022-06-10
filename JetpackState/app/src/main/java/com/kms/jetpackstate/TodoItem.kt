package com.kms.jetpackstate


data class TodoItem(
    val id:Long?,
    val task:String,
){
    constructor(task:String) : this(null, task)
}