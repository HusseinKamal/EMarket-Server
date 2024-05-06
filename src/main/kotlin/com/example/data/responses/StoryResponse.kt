package com.example.data.responses

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class StoryResponse (
    val id:String,
    val text:String,
    val time :String,
    val color : String,
    val userId : String,
)