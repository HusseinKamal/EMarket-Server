package com.example.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class StoryRequest(
    val text:String,
    val time :String,
    val color : String,
    val userId : String
)