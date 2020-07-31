package com.romanenko.dao.mongo.model

import com.romanenko.model.GroupChat
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document
data class MongoChat(
        @Id
        var chatId: String?,
        @Field(CREATOR_ID_LABEL)
        var creatorId: String?,
        @Field(NAME_LABEL)
        var name: String?,
        @Field(INVITED_MEMBERS_LABEL)
        var invitedMembers: Set<String>?,
) {
    constructor(groupChat: GroupChat) : this(groupChat.chatId, groupChat.creatorId, groupChat.name, groupChat.invitedMembers)

    fun toModel(): GroupChat = GroupChat(chatId, creatorId, name, invitedMembers)

    companion object MongoChatFields {
        const val INVITED_MEMBERS_LABEL = "invited"
        const val CHAT_ID_LABEL = "_id"
        const val CREATOR_ID_LABEL = "creatorId"
        const val NAME_LABEL = "name"
    }
}