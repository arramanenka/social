package com.romanenko.dao

import com.romanenko.model.Message
import reactor.core.publisher.Mono

interface MessageDao {
    fun saveMessage(message: Message): Mono<Message>
}