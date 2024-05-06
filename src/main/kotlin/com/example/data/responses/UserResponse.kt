package com.example.data.responses

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class UserResponse (
    val id:String,
    val username:String,
    val email :String,
    val phone :String,
)