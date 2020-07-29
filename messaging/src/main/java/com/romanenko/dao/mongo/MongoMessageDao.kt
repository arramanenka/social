package com.romanenko.dao.mongo

import com.romanenko.dao.ChatDao
import com.romanenko.dao.MessageDao
import com.romanenko.model.Chat
import com.romanenko.model.Message
import com.romanenko.security.Identity
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class MongoMessageDao(
        private val messageRepo: MessageRepo,
        private val chatDao: ChatDao,
        private val reactiveMongoTemplate: ReactiveMongoTemplate
) : MessageDao {

    override fun saveMessage(message: Message): Mono<Message> {
        return chatDao.findChat(message.senderId!!, message.chatId!!)
                .switchIfEmpty(Mono.error<Chat>(HttpClientErrorException(HttpStatus.NOT_FOUND)))
                .flatMap {
                    //todo add 'lastMessageDate' to chat, so as to sort by it
                    if (message.messageId != null) {
                        val query = Query.query(Criteria.where(MongoMessage.MESSAGE_ID_LABEL).`is`(message.messageId)
                                .and(MongoMessage.SENDER_ID_LABEL).`is`(message.senderId))
                        val update = Update().set(MongoMessage.TEXT_LABEL, message.text)
                        return@flatMap reactiveMongoTemplate.findAndModify(query, update, MongoMessage::class.java)
                                .switchIfEmpty(Mono.error<MongoMessage>(HttpClientErrorException(HttpStatus.NOT_FOUND)))
                    }
                    //todo get type of chat and check conditions accordingly
                    messageRepo.save(MongoMessage(message))
                }
                .map { it.toModel() }
    }

    override fun deleteMessage(identity: Identity, chatId: String, messageId: String): Mono<Message> {
        return messageRepo.deleteByChatIdAndMessageIdAndSenderId(chatId, messageId, identity.id)
                .map { it.toModel() }
    }

    override fun getAllMessages(identity: Identity, chatId: String): Flux<Message> {
        return chatDao.findChat(identity.id, chatId)
                .switchIfEmpty(Mono.error<Chat>(HttpClientErrorException(HttpStatus.NOT_FOUND)))
                .flatMapMany {
                    //todo get type of chat and check conditions accordingly
                    messageRepo.findAllByChatIdOrderByCreatedAtDesc(chatId)
                }
                .map { it.toModel() }
    }
}