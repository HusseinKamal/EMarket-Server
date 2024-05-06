package com.example.data.models.userdata

import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.updateOne
import org.litote.kmongo.eq

class MongoUserInfoDataSource(
    db : CoroutineDatabase
) : UserInfoDataSource {

    val users = db.getCollection<UserData>()
    override suspend fun getUserByName(phone: String): UserData? {
        return users.findOne(UserData::phone eq phone)
    }

    override suspend fun insertUser(userData: UserData): Boolean {
        return users.insertOne(userData).wasAcknowledged()
    }

    override suspend fun updateUser(userData: UserData): Boolean {
        return users.updateOne(userData).wasAcknowledged()
    }
}