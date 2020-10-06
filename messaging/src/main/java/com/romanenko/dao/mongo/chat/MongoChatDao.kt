package com.romanenko.dao.mongo.chat

import com.romanenko.dao.ChatDao
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

    override fun updateChatInfo(chat: PrivateChat): Mono<Void> {
        val query = Query.query(Criteria
                .where("interlocutorId").`is`(chat.interlocutorId)
                .and("ownerId").`is`(chat.ownerId)
        )
        val upd = Update.update("lastMessage", chat.lastMessage)
                .set("unreadCount", chat.unreadCount)
                .set("lastMessageText", chat.lastMessageText)
        return reactiveMongoTemplate.upsert(query, upd, MongoChat::class.java).then()
    }
}
