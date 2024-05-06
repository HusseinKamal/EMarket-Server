package com.example.data.models.story

import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class MongoStoryDataSource(
    db : CoroutineDatabase
) : StoryDataSource {

    val storyData = db.getCollection<Story>()

    override suspend fun getStoryById(id: String): Story? {
        return storyData.findOne(Story::id eq ObjectId(id))
    }

    override suspend fun insertStory(story: Story): Boolean {
        return storyData.insertOne(story).wasAcknowledged()
    }

    override suspend fun deleteStory(id: String): Boolean {
        return storyData.deleteOne(Story::id eq ObjectId(id)).wasAcknowledged()
    }

    override suspend fun getAllStory(userId: String): List<Story> {
        val data = storyData.find(Story::userId eq userId)
        return data.descendingSort().toList()
    }
}