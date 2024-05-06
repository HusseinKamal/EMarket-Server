package com.example.data.models.user

interface UserDataSource {
    suspend fun getUserByName(username:String) : User?
    suspend fun insertUser(user: User):Boolean

}
