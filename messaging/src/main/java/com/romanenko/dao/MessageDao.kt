package com.romanenko.dao

import com.romanenko.model.Message
import com.romanenko.security.Identity
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface MessageDao {
    fun saveMessage(message: Message): Mono<Message>
    fun deleteMessage(identity: Identity, chatId: Int, messageId: String): Mono<Message>
    fun getAllMessages(identity: Identity, chatId: Int): Flux<Message>
}