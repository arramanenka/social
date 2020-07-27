package com.romanenko.dao.mongo

import com.romanenko.dao.ChatDao
import com.romanenko.dao.MessageDao
import com.romanenko.model.Chat
import com.romanenko.model.Message
import com.romanenko.security.Identity
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class MongoMessageDao(
        private val messageRepo: MessageRepo,
        private val chatDao: ChatDao
) : MessageDao {

    override fun saveMessage(message: Message): Mono<Message> {
        return chatDao.findChat(message.senderId!!, message.chatId!!)
                .switchIfEmpty(Mono.error<Chat>(HttpClientErrorException(HttpStatus.NOT_FOUND)))
                .flatMap {
                    //todo get type of chat and check conditions accordingly
                    messageRepo.save(MongoMessage(message))
                }
                .map { it.toModel() }
    }

    override fun deleteMessage(identity: Identity, chatId: Int, messageId: Int): Mono<Message> {
        return messageRepo.deleteByChatIdAndMessageIdAndSenderId(chatId, messageId, identity.id)
                .map { it.toModel() }
    }

    override fun getAllMessages(identity: Identity, chatId: Int): Flux<Message> {
        return chatDao.findChat(identity.id, chatId)
                .switchIfEmpty(Mono.error<Chat>(HttpClientErrorException(HttpStatus.NOT_FOUND)))
                .flatMapMany {
                    //todo get type of chat and check conditions accordingly
                    messageRepo.findAllByChatId(chatId)
                }
                .map { it.toModel() }
    }

    override fun deleteAllMessagesOfChat(chatId: Int): Mono<Void> {
        return messageRepo.deleteAllByChatId(chatId)
    }
}