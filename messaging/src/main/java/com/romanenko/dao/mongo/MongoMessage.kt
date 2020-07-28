package com.romanenko.dao.mongo

import com.romanenko.model.Message
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

class MongoMessage(
        @Id
        @Field(MESSAGE_ID_LABEL)
        var messageId: String? = null,
        @Field(SENDER_ID_LABEL)
        var senderId: String? = null,
        @Field(TEXT_LABEL)
        var text: String? = null,
        var chatId: String? = null,
        @CreatedDate
        var createdAt: Date? = null
) {
    fun toModel(): Message = Message(messageId, senderId, text, chatId, createdAt)

    constructor(message: Message) : this(message.messageId, message.senderId, message.text, message.chatId)

    companion object MongoMessageLabels {
        const val MESSAGE_ID_LABEL = "mId"
        const val SENDER_ID_LABEL = "senderId"
        const val TEXT_LABEL = "txt"
    }
}