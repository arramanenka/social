package com.romanenko.dao.mongo.chat

import com.romanenko.model.ChatsMetaInf
import com.romanenko.model.PrivateChat
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface ChatRepo : ReactiveMongoRepository<MongoChat, String> {
    fun findAllByOwnerId(ownerId: String, sort: Sort): Flux<MongoChat>
    fun findByOwnerIdAndInterlocutorId(ownerId: String, interlocutorId: String?): Mono<MongoChat>
}

@Document("chat")
@CompoundIndex(name = "chat_index", def = "{'ownerId' : 1, 'interlocutorId' : 1}")
data class MongoChat(
        @Id
        var id: String? = null,
        var ownerId: String? = null,
        var interlocutorId: String? = null,
        @Indexed(direction = IndexDirection.DESCENDING)
        var lastMessageTime: Date? = null,
        var unreadCount: Long? = null,
        var lastMessageText: String? = null
) {

    fun toModel(): PrivateChat {
        return PrivateChat(ownerId, interlocutorId, lastMessageTime, unreadCount, lastMessageText)
    }

}

data class MongoChatsMetaInf(
        var chatCount: Int?,
        var unreadAmount: Int?
) {

    fun toModel(): ChatsMetaInf = ChatsMetaInf(chatCount ?: 0, unreadAmount ?: 0)
}
