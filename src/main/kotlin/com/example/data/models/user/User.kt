package com.example.data.models.user

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class User(
    val  username:String,
    val  email:String,
    val  phone:String,
    val password :String,
    val salt : String,
    @Contextual
    @BsonId val id :ObjectId = ObjectId()
)
