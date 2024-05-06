package com.example.data.models.userdata

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class UserData(
    val username:String,
    val email :String,
    val phone :String,
    @BsonId val id :ObjectId = ObjectId()
)
