package com.romanenko.dao.mongo.chat

import com.romanenko.model.PrivateChat
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.IndexDirection
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import java.util.*

interface ChatRepo : ReactiveMongoRepository<MongoChat, String> {
    fun findAllByOwnerId(ownerId: String, sort: Sort): Flux<MongoChat>
}

@Document("chat")
@CompoundIndex(name = "chat_index", def = "{'ownerId' : 1, 'interlocutorId' : 1}")
data class MongoChat(
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
