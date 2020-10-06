package com.romanenko.dao.mongo.chat

import com.romanenko.model.PrivateChat
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import java.time.LocalDate

interface ChatRepo : ReactiveMongoRepository<MongoChat, String> {
    fun findAllByOwnerId(ownerId: String): Flux<MongoChat>
}

@Document("chat")
@CompoundIndex(name = "chat_index", def = "{'ownerId' : 1, 'interlocutorId' : 1}")
data class MongoChat(
        var id: String? = null,
        var ownerId: String? = null,
        var interlocutorId: String? = null,
        var lastMessage: LocalDate? = null,
        var unreadCount: Long? = null
) {

    fun toModel(): PrivateChat {
        return PrivateChat(ownerId, interlocutorId, lastMessage, unreadCount)
    }

}
