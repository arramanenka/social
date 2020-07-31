package com.romanenko.dao.mongo

import com.romanenko.connection.ConnectionType
import com.romanenko.dao.ChatDao
import com.romanenko.dao.mongo.model.MongoChat
import com.romanenko.dao.mongo.repository.ChatRepo
import com.romanenko.dao.mongo.repository.MessageRepo
import com.romanenko.model.Chat
import com.romanenko.security.Identity
import com.romanenko.service.ConnectionService
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.stream.Collectors

@Component
class MongoChatDao(
        private val chatRepo: ChatRepo,
        private val messageRepo: MessageRepo,
        private val reactiveMongoTemplate: ReactiveMongoTemplate,
        private val connectionService: ConnectionService
) : ChatDao {

    override fun findChat(userId: String, chatId: String): Mono<Chat> {
        return chatRepo.findByChatIdAndCreatorIdOrMembersContaining(chatId, userId, userId).map { it.toModel() }
    }

    override fun createChat(chat: Chat): Mono<Chat> {
        if (chat.members != null) {
            return Flux.fromIterable(chat.members!!)
                    .filterWhen { connectionService.getConnectionType(chat.creatorId!!, it).map { connection -> connection != ConnectionType.BLACKLIST } }
                    .collect(Collectors.toSet())
                    .flatMap {
                        chat.members = it
                        chatRepo.save(MongoChat(chat))
                    }.map { it.toModel() }
        }
        return chatRepo.save(MongoChat(chat)).map { it.toModel() }
    }

    override fun updateChat(chat: Chat): Mono<Chat> {
        val query = query(where(MongoChat.CHAT_ID_LABEL).`is`(chat.chatId).and(MongoChat.CREATOR_ID_LABEL).`is`(chat.creatorId))
        val update = Update().set(MongoChat.NAME_LABEL, chat.name)
        return reactiveMongoTemplate.findAndModify(query, update, MongoChat::class.java).map { it.toModel() }
    }

    override fun deleteChat(identity: Identity, chatId: String): Mono<Void> {
        return chatRepo.deleteByChatIdAndCreatorId(chatId, identity.id)
                .switchIfEmpty(Mono.error<MongoChat>(HttpClientErrorException(HttpStatus.NOT_FOUND)))
                .flatMap { messageRepo.deleteAllByChatId(chatId) }
    }

    override fun addMember(identity: Identity, chatId: String, userId: String): Mono<Void> {
        return connectionService.getConnectionType(identity.id, userId)
                .flatMap {
                    if (it == ConnectionType.BLACKLIST) {
                        return@flatMap Mono.error<Void>(HttpClientErrorException(HttpStatus.FORBIDDEN))
                    }
                    val query = query(where(MongoChat.CHAT_ID_LABEL).`is`(chatId).and(MongoChat.CREATOR_ID_LABEL).`is`(identity.id))
                    val update = Update().addToSet(MongoChat.MEMBERS_LABEL, userId)
                    reactiveMongoTemplate.updateFirst(query, update, MongoChat::class.java)
                            .filterWhen { result ->
                                if (result.matchedCount == 0L) {
                                    return@filterWhen Mono.error<Boolean>(HttpClientErrorException(HttpStatus.NOT_FOUND))
                                } else if (result.modifiedCount == 0L) {
                                    return@filterWhen Mono.error<Boolean>(HttpClientErrorException(HttpStatus.BAD_REQUEST, "Member already in chat"))
                                }
                                Mono.just(true)
                            }
                            .then()
                }
    }

    override fun removeMember(identity: Identity, chatId: String, userId: String): Mono<Void> {
        val query = query(where(MongoChat.CHAT_ID_LABEL).`is`(chatId).and(MongoChat.CREATOR_ID_LABEL).`is`(identity.id))
        val update = Update().pull(MongoChat.MEMBERS_LABEL, userId)
        return reactiveMongoTemplate.updateFirst(query, update, MongoChat::class.java)
                .filterWhen {
                    if (it.matchedCount == 0L) {
                        return@filterWhen Mono.error<Boolean>(HttpClientErrorException(HttpStatus.NOT_FOUND))
                    } else if (it.modifiedCount == 0L) {
                        return@filterWhen Mono.error<Boolean>(HttpClientErrorException(HttpStatus.BAD_REQUEST, "Member is not in chat"))
                    }
                    Mono.just(true)
                }
                .then()
    }

    override fun getOwnChats(identity: Identity): Flux<Chat> {
        return chatRepo.findAllByMembersContainingOrCreatorId(identity.id, identity.id).map { it.toModel() }
    }
}