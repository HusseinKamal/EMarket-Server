package com.example.data.models.userdata

import com.example.data.models.userdata.UserData

interface UserInfoDataSource {
    suspend fun getUserByName(phone:String) : UserData?
    suspend fun insertUser(userData: UserData):Boolean
    suspend fun updateUser(userData: UserData):Boolean

}
