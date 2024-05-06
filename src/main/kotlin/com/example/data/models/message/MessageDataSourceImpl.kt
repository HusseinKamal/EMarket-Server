package com.example.data.models.message

import com.example.data.models.message.Message
import com.example.data.models.message.MessageDataSource
import org.litote.kmongo.coroutine.CoroutineDatabase

class MessageDataSourceImpl(
    private val db:CoroutineDatabase
) : MessageDataSource {

    private val messages = db.getCollection<Message>()
    override suspend fun getAllMessages(): List<Message> {
        return messages.find()
            .descendingSort(Message::timeStamp)
            .toList()
    }

    override suspend fun insertMessage(message: Message) {
        messages.insertOne(message)
    }
}