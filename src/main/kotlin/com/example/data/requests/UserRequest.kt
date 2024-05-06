package com.example.data.requests

import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class UserRequest(
    val id:String = ObjectId().toString(),
    val username:String,
    val email :String,
    val phone :String,
)
