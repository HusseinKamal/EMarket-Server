package com.example.data.responses

import com.example.data.models.user.User
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse (
    val token:String,
    val  username:String,
    val  email:String,
    val  phone:String,
)