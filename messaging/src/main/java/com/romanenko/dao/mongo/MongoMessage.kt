package com.romanenko.dao.mongo

import com.romanenko.model.Message
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "messages")
@CompoundIndex(name = "user_index", def = "{'senderId' : 1, 'receiverId' : 1}")
class MongoMessage(
        @Id
        var messageId: String? = null,
        var senderId: String? = null,
        var receiverId: String? = null,
        var text: String? = null,
        @CreatedDate
        var createdAt: Date? = null
) {
    fun toModel(): Message = Message(messageId, senderId, receiverId, text, createdAt)

    constructor(message: Message) : this(message.messageId, message.senderId, message.receiverId, message.text)
}