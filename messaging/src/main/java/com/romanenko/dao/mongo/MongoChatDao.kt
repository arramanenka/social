package com.romanenko.dao.mongo

import com.romanenko.dao.ChatDao
import com.romanenko.model.Chat
import com.romanenko.security.Identity
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Component
class MongoChatDao(
        private val chatRepo: ChatRepo,
        private val messageRepo: MessageRepo,
        private val reactiveMongoTemplate: ReactiveMongoTemplate
) : ChatDao {

    override fun findChat(userId: String, chatId: String): Mono<Chat> {
        return chatRepo.findByChatIdAndCreatorIdOrMembersContaining(chatId, userId, userId).map { it.toModel() }
    }

    override fun createChat(chat: Chat): Mono<Chat> {
        return chatRepo.save(MongoChat(chat)).map { it.toModel() }
    }

    override fun updateChat(chat: Chat): Mono<Chat> {
        val query = query(where(MongoChat.CHAT_ID_LABEL).`is`(chat.chatId).and(MongoChat.CREATOR_ID_LABEL).`is`(chat.creatorId))
        val update = Update().set(MongoChat.NAME_LABEL, chat.name)
        return reactiveMongoTemplate.findAndModify(query, update, MongoChat::class.java).map { it.toModel() }
    }

    override fun deleteChat(identity: Identity, chatId: String): Mono<Void> {
        return chatRepo.deleteByChatIdAndCreatorId(chatId, identity.id)
                .doOnSuccess {
                    messageRepo.deleteAllByChatId(chatId).subscribeOn(Schedulers.parallel()).subscribe()
                }
                .switchIfEmpty(Mono.error<Void>(HttpClientErrorException(HttpStatus.NOT_FOUND)))
    }

    override fun addMember(identity: Identity, chatId: String, userId: String): Mono<Void> {
        val query = query(where(MongoChat.CHAT_ID_LABEL).`is`(chatId).and(MongoChat.CREATOR_ID_LABEL).`is`(identity.id))
        val update = Update().addToSet(MongoChat.MEMBERS_LABEL, userId)
        return reactiveMongoTemplate.updateFirst(query, update, MongoChat::class.java).then()
    }

    override fun removeMember(identity: Identity, chatId: String, userId: String): Mono<Void> {
        val query = query(where(MongoChat.CHAT_ID_LABEL).`is`(chatId).and(MongoChat.CREATOR_ID_LABEL).`is`(identity.id))
        val update = Update().pull(MongoChat.MEMBERS_LABEL, userId)
        return reactiveMongoTemplate.updateFirst(query, update, MongoChat::class.java).then()
    }

    override fun getOwnChats(identity: Identity): Flux<Chat> {
        return chatRepo.findAllByMembersContaining(identity.id).map { it.toModel() }
    }
}