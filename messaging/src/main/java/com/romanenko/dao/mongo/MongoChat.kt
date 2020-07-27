package com.romanenko.dao.mongo

import com.romanenko.model.Chat
import com.romanenko.model.ChatType
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class MongoChat(
        @Id
        var chatId: Int?,
        var creatorId: String?,
        var name: String?,
        var members: List<String>?,
        var type: ChatType?
) {
    constructor(chat: Chat) : this(chat.chatId, chat.creatorId, chat.name, chat.members, chat.type)

    fun toModel(): Chat {
        return Chat(chatId, creatorId, name, members, type)
    }
}