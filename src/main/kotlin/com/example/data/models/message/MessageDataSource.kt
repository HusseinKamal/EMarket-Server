package com.example.data.models.message

import com.example.data.models.message.Message

interface MessageDataSource {
    suspend fun getAllMessages():List<Message>

    suspend fun insertMessage(message: Message)
}