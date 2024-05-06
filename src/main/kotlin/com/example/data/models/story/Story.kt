package com.example.data.models.story
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Story(
    val text:String,
    val time :String,
    val color : String,
    val userId : String,
    @BsonId val id : ObjectId = ObjectId()
){

}