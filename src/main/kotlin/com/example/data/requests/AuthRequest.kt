package com.example.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val username: String="",
    val password: String="",
    val email: String="",
    val phone:String="",
    val confirmPassword: String=""
)
