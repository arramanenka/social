package com.romanenko.dao.mongo

import com.romanenko.model.Message
import org.springframework.data.annotation.Id

class MongoMessage(
        @Id
        var messageId: Int? = null,
        var senderId: String? = null,
        var text: String? = null,
        var chatId: Int? = null
) {
    fun toModel(): Message = Message(messageId, senderId, text, chatId)

    constructor(message: Message) : this(message.messageId, message.senderId, message.text, message.chatId)
}