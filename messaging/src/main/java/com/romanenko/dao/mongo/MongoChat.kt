package com.romanenko.dao.mongo

import com.romanenko.model.Chat
import com.romanenko.model.ChatType
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document
data class MongoChat(
        @Id
        @Field(CHAT_ID_LABEL)
        var chatId: Int?,
        @Field(CREATOR_ID_LABEL)
        var creatorId: String?,
        @Field(NAME_LABEL)
        var name: String?,
        @Field(MEMBERS_LABEL)
        var members: Set<String>?,
        var type: ChatType?
) {
    constructor(chat: Chat) : this(chat.chatId, chat.creatorId, chat.name, chat.members, chat.type)

    fun toModel(): Chat = Chat(chatId, creatorId, name, members, type)

    companion object MongoChatFields {
        const val MEMBERS_LABEL = "members"
        const val CHAT_ID_LABEL = "chatId"
        const val CREATOR_ID_LABEL = "creatorId"
        const val NAME_LABEL = "name"
    }
}