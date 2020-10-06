package com.romanenko.dao.mongo.chat

import com.romanenko.dao.ChatDao
import com.romanenko.model.Message
import com.romanenko.model.PrivateChat
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class MongoChatDao(
        private val chatRepo: ChatRepo,
        private val reactiveMongoTemplate: ReactiveMongoTemplate
) : ChatDao {
    override fun getChats(ownerId: String): Flux<PrivateChat> {
        return chatRepo.findAllByOwnerId(ownerId, Sort.by(Sort.Direction.DESC, "lastMessage"))
                .map { it.toModel() }
    }

    override fun addLastMessageInfo(message: Message): Mono<Void> {
        val query = Query.query(Criteria
                .where("interlocutorId").`is`(message.senderId)
                .and("ownerId").`is`(message.receiverId)
        )
        val upd = Update.update("lastMessageTime", message.createdAt)
                .inc("unreadCount", 1)
                .set("lastMessageText", message.text)
        return reactiveMongoTemplate.upsert(query, upd, MongoChat::class.java).then()
    }
}
