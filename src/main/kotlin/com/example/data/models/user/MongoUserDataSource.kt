package com.example.data.models.user

import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class MongoUserDataSource(
    db : CoroutineDatabase
) : UserDataSource {

    val users = db.getCollection<User>()
    override suspend fun getUserByName(username: String): User? {
        if(users.find().toList().isEmpty()){
            return null
        }
        return users.findOne(User::email eq username)
    }

    override suspend fun insertUser(user: User): Boolean {
        return users.insertOne(user).wasAcknowledged()
    }
}