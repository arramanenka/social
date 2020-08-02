package com.romanenko.service.impl

import com.romanenko.dao.MessageDao
import com.romanenko.io.PageQuery
import com.romanenko.model.Message
import com.romanenko.security.Identity
import com.romanenko.service.MessageService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class DefaultMessageService(
        private val messageDao: MessageDao
) : MessageService {
    override fun sendMessage(message: Mono<Message>): Mono<Message> {
        return message.flatMap {
            if (it.areUsersNotValid()) {
                return@flatMap Mono.error<Message>(HttpClientErrorException(HttpStatus.BAD_REQUEST, "Invalid users of direct chat"))
            }
            if (it.isTextNotValid()) {
                return@flatMap Mono.error<Message>(HttpClientErrorException(HttpStatus.BAD_REQUEST, "Message is blank"))
            }
            messageDao.sendMessage(message)
        }
    }

    override fun deleteMessage(message: Message): Mono<Message> {
        if (message.areUsersNotValid()) {
            return Mono.error<Message>(HttpClientErrorException(HttpStatus.BAD_REQUEST, "Invalid users of direct chat"))
        }
        if (message.messageId.isNullOrEmpty()) {
            return Mono.error<Message>(HttpClientErrorException(HttpStatus.BAD_REQUEST, "Invalid message id"))
        }
        return messageDao.deleteMessage(message)
    }

    override fun getMessages(identity: Identity, userId: String, pageQuery: PageQuery): Flux<Message> {
        if (identity.id == userId) {
            return Flux.error(HttpClientErrorException(HttpStatus.BAD_REQUEST, "Invalid user of direct chat"))
        }
        return messageDao.getMessages(identity.id, userId, pageQuery)
    }
}