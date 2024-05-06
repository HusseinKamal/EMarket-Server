package com.example.data.models.story

import com.example.data.models.story.Story

interface StoryDataSource {
    suspend fun getStoryById(id:String) : Story?
    suspend fun insertStory(story: Story):Boolean
    suspend fun deleteStory(id: String):Boolean
    suspend fun getAllStory(userId:String) : List<Story>?
}
